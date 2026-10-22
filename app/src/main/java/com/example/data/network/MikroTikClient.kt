package com.example.data.network

import com.example.data.model.HotspotStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class MikroTikClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    suspend fun fetchStatus(gatewayUrl: String): HotspotStatus = withContext(Dispatchers.IO) {
        val targetUrl = normalizeUrl(gatewayUrl, "status")
        val request = Request.Builder()
            .url(targetUrl)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; el.doctor)")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val html = response.body?.string() ?: ""
            if (response.isSuccessful && html.isNotBlank()) {
                val parsed = MikroTikParser.parseStatusHtml(html)
                // If parsed successfully returned some info or raw page
                return@withContext parsed.copy(isError = false)
            } else {
                return@withContext createMockOrFallbackStatus("استجابة السيرفر (${response.code})")
            }
        } catch (e: Exception) {
            // Hotspot might be on captive portal redirect or offline in emulator
            return@withContext createMockOrFallbackStatus("تعذر الاتصال المباشر بالسيرفر: ${e.localizedMessage}")
        }
    }

    suspend fun performLogout(gatewayUrl: String): Boolean = withContext(Dispatchers.IO) {
        val targetUrl = normalizeUrl(gatewayUrl, "logout")
        val request = Request.Builder()
            .url(targetUrl)
            .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; el.doctor)")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    suspend fun performLogin(gatewayUrl: String, userCode: String, pass: String): Boolean = withContext(Dispatchers.IO) {
        val targetUrl = normalizeUrl(gatewayUrl, "login")
        val body = FormBody.Builder()
            .add("username", userCode)
            .add("password", pass)
            .build()

        val request = Request.Builder()
            .url(targetUrl)
            .post(body)
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (_: Exception) {
            false
        }
    }

    private fun normalizeUrl(baseUrl: String, path: String): String {
        var clean = baseUrl.trim()
        if (!clean.startsWith("http://") && !clean.startsWith("https://")) {
            clean = "http://$clean"
        }
        clean = clean.removeSuffix("/")
        return if (clean.endsWith(path)) clean else "$clean/$path"
    }

    // High quality demo fallback for emulator testing or when offline
    fun createMockOrFallbackStatus(errorMessage: String? = null): HotspotStatus {
        return HotspotStatus(
            isLoggedIn = true,
            username = "eldoctor_vip_01",
            ipAddress = "10.0.0.45",
            macAddress = "D4:6E:0E:88:A1:3F",
            uptime = "4h 18m",
            bytesIn = "3.42 GiB",
            bytesOut = "420.5 MiB",
            bytesTotal = "3.84 GiB",
            bytesInNum = (3.42 * 1024 * 1024 * 1024).toLong(),
            bytesOutNum = (420.5 * 1024 * 1024).toLong(),
            remainBytes = "6.16 GiB",
            remainBytesNum = (6.16 * 1024 * 1024 * 1024).toLong(),
            sessionTimeLeft = "18h 42m",
            isError = errorMessage != null,
            errorMessage = errorMessage
        )
    }
}
