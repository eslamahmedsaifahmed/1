package com.example.data.network

import com.example.data.model.HotspotStatus
import com.example.data.util.NetworkUtils
import java.util.regex.Pattern

object MikroTikParser {

    fun parseStatusHtml(html: String): HotspotStatus {
        if (html.isBlank()) {
            return HotspotStatus(isError = true, errorMessage = "لم يتم استلام رد من صفحة السيرفر")
        }

        val lowerHtml = html.lowercase()
        val isLoginPage = lowerHtml.contains("action=\"login\"") ||
                (lowerHtml.contains("action=\"http://") && lowerHtml.contains("/login")) ||
                (lowerHtml.contains("name=\"username\"") && lowerHtml.contains("name=\"password\""))

        val isStatusPage = lowerHtml.contains("action=\"logout\"") ||
                (lowerHtml.contains("action=\"http://") && lowerHtml.contains("/logout")) ||
                lowerHtml.contains("bytes-in") || lowerHtml.contains("uptime") ||
                lowerHtml.contains("bytes up/down") || lowerHtml.contains("connected / uptime")

        val isLoggedIn = isStatusPage && !isLoginPage

        // IP address
        val ip = extractPattern(html, """(?i)ip\s*(?:address)?[^0-9]*([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3})""")
            ?: extractPattern(html, """\b(10\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}|192\.168\.[0-9]{1,3}\.[0-9]{1,3}|172\.(?:1[6-9]|2[0-9]|3[01])\.[0-9]{1,3}\.[0-9]{1,3})\b""")

        // MAC address
        val mac = extractPattern(html, """(?i)([0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2}[:-][0-9A-Fa-f]{2})""")

        // Username
        val username = extractPattern(html, """(?i)(?:user|username|المستخدم|اسم المستخدم)[^a-zA-Z0-9_\-\.\@]*([a-zA-Z0-9_\-\.\@]+)""")

        // Uptime: e.g. 2h15m, 45m20s, 01:23:45
        val uptime = extractPattern(html, """(?i)(?:uptime|connected)[^0-9]*([0-9]+[wdhms][0-9wdhms\s]*)""")
            ?: extractPattern(html, """(?i)(?:الوقت المنقضي|مدة الاتصال)[^0-9]*([0-9]+[wdhms][0-9wdhms\s]*)""")
            ?: extractPattern(html, """(?i)([0-9]{1,2}:[0-9]{2}:[0-9]{2})""")

        // Bytes up / down
        var bytesIn: String? = null
        var bytesOut: String? = null

        val upDownMatcher = Pattern.compile(
            """(?i)bytes\s*up/down[^0-9]*([0-9\.]+\s*[a-zA-Z]+)\s*/\s*([0-9\.]+\s*[a-zA-Z]+)""",
            Pattern.CASE_INSENSITIVE
        ).matcher(html)

        if (upDownMatcher.find()) {
            bytesOut = upDownMatcher.group(1)?.trim()
            bytesIn = upDownMatcher.group(2)?.trim()
        }

        if (bytesIn == null) {
            bytesIn = extractPattern(html, """(?i)(?:bytes-in|download|التحميل|تنزيل)[^0-9]*([0-9\.]+\s*[a-zA-Z]+)""")
        }
        if (bytesOut == null) {
            bytesOut = extractPattern(html, """(?i)(?:bytes-out|upload|الرفع|تحميل)[^0-9]*([0-9\.]+\s*[a-zA-Z]+)""")
        }

        // Remaining bytes / quota
        val remainBytes = extractPattern(html, """(?i)(?:remain-bytes|remaining\s*bytes|الرصيد المتبقي|المتبقي|رصيد الكارت)[^0-9]*([0-9\.]+\s*[a-zA-Z]+)""")

        // Session time left
        val timeLeft = extractPattern(html, """(?i)(?:session-time-left|time\s*left|الوقت المتبقي|صلاحية الكارت)[^0-9]*([0-9]+[wdhms][0-9wdhms\s]*)""")

        val bytesInNum = NetworkUtils.parseByteStringToLong(bytesIn)
        val bytesOutNum = NetworkUtils.parseByteStringToLong(bytesOut)
        val remainBytesNum = NetworkUtils.parseByteStringToLong(remainBytes)

        return HotspotStatus(
            isLoggedIn = isLoggedIn || (bytesInNum > 0 || !uptime.isNullOrBlank()),
            username = username,
            ipAddress = ip,
            macAddress = mac,
            uptime = uptime,
            bytesIn = bytesIn,
            bytesOut = bytesOut,
            bytesInNum = bytesInNum,
            bytesOutNum = bytesOutNum,
            remainBytes = remainBytes,
            remainBytesNum = remainBytesNum,
            sessionTimeLeft = timeLeft,
            rawHtml = html,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun extractPattern(text: String, regex: String): String? {
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE or Pattern.MULTILINE)
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            if (matcher.groupCount() >= 1) matcher.group(1)?.trim() else matcher.group(0)?.trim()
        } else {
            null
        }
    }
}
