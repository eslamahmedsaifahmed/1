package com.example.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import com.example.data.model.NetworkGatewayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Locale

object NetworkUtils {

    fun getNetworkGatewayInfo(context: Context, customGatewayOverride: String? = null): NetworkGatewayInfo {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val activeNetwork = connectivityManager?.activeNetwork
        val capabilities = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        var detectedGateway = "10.0.0.1"
        var deviceIp = "10.0.0.2"
        var ssid = if (isWifi) "متصل بالواي فاي" else "غير متصل بالواي فاي"
        var linkSpeed = 0

        if (isWifi && wifiManager != null) {
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo != null) {
                val rawSsid = wifiInfo.ssid?.replace("\"", "") ?: ""
                if (rawSsid.isNotEmpty() && rawSsid != "<unknown ssid>") {
                    ssid = rawSsid
                }
                linkSpeed = wifiInfo.linkSpeed
            }

            val dhcpInfo = wifiManager.dhcpInfo
            if (dhcpInfo != null && dhcpInfo.gateway != 0) {
                detectedGateway = formatIpAddress(dhcpInfo.gateway)
                if (dhcpInfo.ipAddress != 0) {
                    deviceIp = formatIpAddress(dhcpInfo.ipAddress)
                }
            } else {
                // Try from link properties
                val linkProperties = connectivityManager?.getLinkProperties(activeNetwork)
                linkProperties?.routes?.firstOrNull { it.isDefaultRoute && it.gateway != null }?.let { route ->
                    detectedGateway = route.gateway?.hostAddress ?: detectedGateway
                }
            }
        }

        val effectiveGateway = if (!customGatewayOverride.isNullOrBlank()) {
            customGatewayOverride.trim()
        } else {
            detectedGateway
        }

        return NetworkGatewayInfo(
            isWifiConnected = isWifi,
            ssid = ssid,
            gatewayIp = effectiveGateway,
            deviceIp = deviceIp,
            linkSpeedMbps = linkSpeed
        )
    }

    private fun formatIpAddress(ip: Int): String {
        return String.format(
            Locale.US,
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
    }

    suspend fun pingGateway(host: String, port: Int = 80, timeoutMs: Int = 2000): Long? = withContext(Dispatchers.IO) {
        val cleanHost = host
            .removePrefix("http://")
            .removePrefix("https://")
            .substringBefore("/")
            .substringBefore(":")

        val startTime = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(cleanHost, port), timeoutMs)
                val elapsed = System.currentTimeMillis() - startTime
                return@withContext elapsed
            }
        } catch (_: Exception) {
            // Try ICMP reachability as fallback
            try {
                val address = InetAddress.getByName(cleanHost)
                val reached = address.isReachable(timeoutMs)
                if (reached) {
                    return@withContext System.currentTimeMillis() - startTime
                }
            } catch (_: Exception) {
            }
            null
        }
    }

    fun formatBytesToArabic(bytes: Long): String {
        if (bytes <= 0L) return "0 ميجابايت"
        val kb = bytes / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0

        return when {
            gb >= 1.0 -> String.format(Locale.US, "%.2f جيجابايت", gb)
            mb >= 1.0 -> String.format(Locale.US, "%.1f ميجابايت", mb)
            else -> String.format(Locale.US, "%.0f كيلوبايت", kb)
        }
    }

    fun parseByteStringToLong(text: String?): Long {
        if (text.isNullOrBlank()) return 0L
        val clean = text.trim().uppercase(Locale.US)
        val numberPart = Regex("""[0-9]+(\.[0-9]+)?""").find(clean)?.value?.toDoubleOrNull() ?: return 0L

        return when {
            clean.contains("GIB") || clean.contains("GB") || clean.contains("جيجا") -> (numberPart * 1024.0 * 1024.0 * 1024.0).toLong()
            clean.contains("MIB") || clean.contains("MB") || clean.contains("ميجا") -> (numberPart * 1024.0 * 1024.0).toLong()
            clean.contains("KIB") || clean.contains("KB") || clean.contains("كيلو") -> (numberPart * 1024.0).toLong()
            clean.contains("B") -> numberPart.toLong()
            else -> numberPart.toLong()
        }
    }
}
