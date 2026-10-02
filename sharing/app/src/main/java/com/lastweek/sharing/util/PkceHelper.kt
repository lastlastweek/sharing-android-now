package com.lastweek.sharing.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PkceHelper {
    private val secureRandom = SecureRandom()

    /**
     * 生成 Code Verifier
     * 使用 SecureRandom 生成 32 字节随机数，并转换为 URL-safe Base64 字符串
     */
    fun generateCodeVerifier(): String {
        val code = ByteArray(32)
        secureRandom.nextBytes(code)
        // 使用 URL-safe Base64 编码，不带 padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code)
    }

    /**
     * 根据 Code Verifier 生成 Code Challenge (S256)
     */
    fun generateCodeChallenge(codeVerifier: String): String {
        // 1. 获取 SHA-256 实例
        val digest = MessageDigest.getInstance("SHA-256")

        // 2. 计算哈希值 (输入需转为 ASCII 字节)
        val hash = digest.digest(codeVerifier.toByteArray(Charsets.US_ASCII))

        // 3. URL-safe Base64 编码并移除 padding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
    }
}
