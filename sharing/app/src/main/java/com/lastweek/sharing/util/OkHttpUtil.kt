package com.lastweek.sharing.util

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.lastweek.sharing.AppConfig
import com.lastweek.sharing.handler.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object OkHttpUtil {
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private const val BASE_URL = "https://你的服务器地址"

    private lateinit var tokenManager: TokenManager

    // 防止多个请求同时刷新 Token
    private val refreshLock = Any()

    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
    }

    /**
     * 自动注入 Authorization 请求头的拦截器
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
        // 本地有 Token 且请求未显式注入时，自动追加 Bearer Header
        if (!accessToken.isNullOrEmpty() && originalRequest.header("Authorization") == null) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        chain.proceed(requestBuilder.build())
    }

    /**
     * 401 自动刷新 Access Token
     */
    private val tokenAuthenticator = object : Authenticator {
        override fun authenticate(
            route: Route?,
            response: Response
        ): Request? {

            // 防止无限重试
            if (responseCount(response) >= 2) {
                tokenManager.clearTokens()
                return null
            }

            // 记录触发 401 时使用的旧 Token
            val oldToken = response.request.header("Authorization")?.removePrefix("Bearer ")?.trim()

            synchronized(refreshLock) {
                // 再次读取当前 Token
                val currentToken = tokenManager.getAccessToken()

                // 其他请求已经刷新成功，直接使用新 Token 重试
                if (!currentToken.isNullOrEmpty() && !oldToken.isNullOrEmpty() && currentToken != oldToken) {
                    return response.request
                        .newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                // 没有 Refresh Token，无法刷新
                val refreshToken = tokenManager.getRefreshToken()
                if (refreshToken.isNullOrEmpty()) {
                    tokenManager.clearTokens()
                    return null
                }

                val newTokens = refreshAccessTokenSync(refreshToken, response.request.url.toString())

                if (newTokens == null) {
                    tokenManager.clearTokens()
                    return null
                }

                val newAccessToken = newTokens.first
                val newRefreshToken = newTokens.second

                // 保存新 Token
                tokenManager.saveTokens(
                    newAccessToken,
                    newRefreshToken
                )

                // 使用新 Access Token 重试原请求
                return response.request
                    .newBuilder()
                    .header(
                        "Authorization",
                        "Bearer $newAccessToken"
                    )
                    .build()
            }
        }
    }

    // 单例 OkHttpClient 配置
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    suspend fun loginEntire(json: String): String = withContext(Dispatchers.IO) {
        val verifier = PkceHelper.generateCodeVerifier()
        val challenge = PkceHelper.generateCodeChallenge(verifier)

        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        var session: String
        var code: String

        val requestGetSessionURL = AppConfig.GENERIC_SERVER_DEV + "/api/auth/login"
        val requestGetSession = Request.Builder().url(requestGetSessionURL).post(body).build()
        client.newCall(requestGetSession).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            val jsonObject = JSONObject(response.body.string())
            val resCode = jsonObject.optInt("resCode")
            val resMsg = jsonObject.optString("resMsg")
            println("resCode = ${resCode}")
            println("resMsg = ${resMsg}")

            // 找服务器返回的 Set-Cookie
            val setCookies = response.headers("Set-Cookie")
            session = setCookies
                .firstOrNull { it.startsWith("JSESSIONID=") }
                ?.substringBefore(";")
                ?: ""
        }

        val requestGetCodeURL = (AppConfig.GENERIC_SERVER_DEV + "/oauth2/authorize")
            .toUri().buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", "androidClientID")
            .appendQueryParameter("redirect_uri", "https://oauth.pstmn.io/v1/callback")
            .appendQueryParameter("state", "randomstring")
            .appendQueryParameter("scope", "openid offline_access")
            .appendQueryParameter("prompt", "none")
            .appendQueryParameter("code_challenge", challenge)
            .appendQueryParameter("code_challenge_method", "S256")
            .build()
            .toString()
        val requestGetCode = Request.Builder().url(requestGetCodeURL).get().addHeader("Cookie", session).build()
        client.newCall(requestGetCode).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            val finalUrl = response.request.url

            code = finalUrl.queryParameter("code").toString()
            Log.d("OAuth", "code = $code")
        }

        val requestGetAccessTokenURL = (AppConfig.GENERIC_SERVER_DEV + "/oauth2/token")
            .toUri().buildUpon()
            .appendQueryParameter("grant_type", "authorization_code")
            .appendQueryParameter("client_id", "androidClientID")
            .appendQueryParameter("redirect_uri", "https://oauth.pstmn.io/v1/callback")
            .appendQueryParameter("code_verifier", verifier)
            .appendQueryParameter("code", code)
            .build()
            .toString()

        val requestGetAccessToken = Request.Builder().url(requestGetAccessTokenURL).post(body).build()
        client.newCall(requestGetAccessToken).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            response.body.string()
        }
    }

    suspend fun post(url: String, json: String): String = withContext(Dispatchers.IO) {
        val body = json.toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            response.body.string()
        }
    }

    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).get().build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            response.body.string()
        }
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    fun clearTokens() {
        tokenManager.clearTokens()
    }

    private fun refreshAccessTokenSync(refreshToken: String, currentUrl: String): Pair<String, String>? {
        return try {
            val refreshClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val json = JSONObject().apply {
                put("refresh_token", refreshToken)
            }
            val body = json.toString().toRequestBody(JSON_MEDIA_TYPE)

            val refreshUrl = "$BASE_URL/api/v1/auth/refresh"

            val request = Request.Builder().url(refreshUrl).post(body).build()

            refreshClient.newCall(request).execute().use { res ->
                if (res.isSuccessful) {
                    val resBody = res.body.string()
                    val jsonObj = JSONObject(resBody)
                    val dataObj = jsonObj.optJSONObject("data") ?: jsonObj

                    val newAccess = dataObj.optString("access_token")
                    val newRefresh = dataObj.optString("refresh_token")

                    if (newAccess.isNotEmpty() && newRefresh.isNotEmpty()) {
                        Pair(newAccess, newRefresh)
                    } else null
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse

        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }

        return count
    }
}