package com.lastweek.sharing.service

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class SecurityInterceptor(
    private val appKey: String,
    private val appSecret: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. 生成基础参数
        val timestamp = System.currentTimeMillis().toString()
        val nonce = UUID.randomUUID().toString().replace("-", "")
        val bodyString = getRequestBodyString(originalRequest)

        // 2. 拼接待签名字符串
        // 规则: appKey=xxx&body=xxx&nonce=xxx&timestamp=xxx&appSecret=xxx
        val rawToSign = "appKey=$appKey&body=$bodyString&nonce=$nonce&timestamp=$timestamp&appSecret=$appSecret"

        // 3. 计算 HMAC-SHA256 签名
        val signature = hmacSha256(rawToSign, appSecret)

        // 4. 将鉴权参数注入请求头 Header
        val newRequest = originalRequest.newBuilder()
            .addHeader("X-App-Key", appKey)
            .addHeader("X-Timestamp", timestamp)
            .addHeader("X-Nonce", nonce)
            .addHeader("X-Signature", signature)
            .build()

        return chain.proceed(newRequest)
    }

    private fun getRequestBodyString(request: okhttp3.Request): String {
        val body = request.body ?: return ""
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun hmacSha256(data: String, key: String): String {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
        sha256HMAC.init(secretKey)
        val hash = sha256HMAC.doFinal(data.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }
}