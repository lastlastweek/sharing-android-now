package com.lastweek.sharing.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastweek.sharing.util.OkHttpUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    // Compose 原生状态：外部可读取，内部独占修改权
    var isLoading by mutableStateOf(false)
        private set

    fun login(
        mobile: String,
        smsCode: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (isLoading) {
            return
        }
        viewModelScope.launch {
            isLoading = true
            try {
                val (isSuccess, errorMsg) = withContext(Dispatchers.IO) {
                    val json = JSONObject().apply {
                        put("mobile", mobile)
                        put("smsCode", smsCode)
                    }

                    val rawResult = OkHttpUtil.loginEntire(
                        json = json.toString()
                    )

                    val response = JSONObject(rawResult)
                    val accessToken = response.optString("access_token", "")

                    if (accessToken.isNotEmpty()) {
                        val refreshToken = ""
                        OkHttpUtil.saveTokens(accessToken, refreshToken)
                        Pair(true, null)
                    } else {
                        Pair(false, "登录失败")
                    }
                }

                if (isSuccess) {
                    onSuccess()
                } else {
                    onError(errorMsg ?: "登录失败")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "网络异常，请检查网络设置")
            } finally {
                isLoading = false
            }
        }
    }
}
