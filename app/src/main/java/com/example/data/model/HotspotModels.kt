package com.example.data.model

data class HotspotStatus(
    val isLoggedIn: Boolean = false,
    val username: String? = null,
    val ipAddress: String? = null,
    val macAddress: String? = null,
    val uptime: String? = null,
    val bytesIn: String? = null,        // Download formatted (e.g. "3.2 GiB")
    val bytesOut: String? = null,       // Upload formatted (e.g. "450 MiB")
    val bytesTotal: String? = null,
    val bytesInNum: Long = 0L,
    val bytesOutNum: Long = 0L,
    val remainBytes: String? = null,    // Remaining quota formatted
    val remainBytesNum: Long = 0L,
    val sessionTimeLeft: String? = null,
    val refreshInterval: String? = null,
    val rawHtml: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val errorMessage: String? = null
) {
    val totalBytesNum: Long get() = bytesInNum + bytesOutNum

    val totalBytesInGb: Double get() = bytesInNum.toDouble() / (1024.0 * 1024.0 * 1024.0)
    val totalBytesOutGb: Double get() = bytesOutNum.toDouble() / (1024.0 * 1024.0 * 1024.0)
    val totalUsedGb: Double get() = totalBytesNum.toDouble() / (1024.0 * 1024.0 * 1024.0)
    val remainGb: Double get() = if (remainBytesNum > 0) remainBytesNum.toDouble() / (1024.0 * 1024.0 * 1024.0) else 0.0
}

data class NetworkGatewayInfo(
    val isWifiConnected: Boolean = false,
    val ssid: String = "غير متصل",
    val gatewayIp: String = "10.0.0.1",
    val deviceIp: String = "10.0.0.2",
    val pingLatencyMs: Long? = null,
    val isGatewayReachable: Boolean = false,
    val linkSpeedMbps: Int = 0
)

enum class HotspotTab {
    DASHBOARD,
    HOTSPOT_WEBVIEW,
    CARDS_WALLET,
    DIAGNOSTICS
}
