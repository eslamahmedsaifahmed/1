package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CardEntity
import com.example.data.model.HotspotStatus
import com.example.data.model.HotspotTab
import com.example.data.model.NetworkGatewayInfo
import com.example.data.network.MikroTikClient
import com.example.data.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HotspotViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val cardDao = db.cardDao()
    private val client = MikroTikClient()

    private val _hotspotStatus = MutableStateFlow(HotspotStatus())
    val hotspotStatus: StateFlow<HotspotStatus> = _hotspotStatus.asStateFlow()

    private val _networkInfo = MutableStateFlow(NetworkGatewayInfo())
    val networkInfo: StateFlow<NetworkGatewayInfo> = _networkInfo.asStateFlow()

    private val _activeTab = MutableStateFlow(HotspotTab.DASHBOARD)
    val activeTab: StateFlow<HotspotTab> = _activeTab.asStateFlow()

    private val _customGateway = MutableStateFlow("10.0.0.1")
    val customGateway: StateFlow<String> = _customGateway.asStateFlow()

    private val _userTargetQuotaGb = MutableStateFlow(10.0)
    val userTargetQuotaGb: StateFlow<Double> = _userTargetQuotaGb.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _actionMessage = MutableStateFlow<String?>(null)
    val actionMessage: StateFlow<String?> = _actionMessage.asStateFlow()

    val savedCards: StateFlow<List<CardEntity>> = cardDao.getAllCards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        detectNetwork()
        refreshStatus()
    }

    fun detectNetwork() {
        val info = NetworkUtils.getNetworkGatewayInfo(getApplication(), _customGateway.value)
        _networkInfo.value = info
    }

    fun refreshStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            detectNetwork()
            val gw = _networkInfo.value.gatewayIp

            if (_isDemoMode.value) {
                _hotspotStatus.value = client.createMockOrFallbackStatus(null)
                _isLoading.value = false
                return@launch
            }

            val status = client.fetchStatus(gw)
            if (status.isError) {
                // If router cannot be reached (e.g. running in emulator or disconnected),
                // supply a rich demo snapshot so the UI can still be fully tested!
                _hotspotStatus.value = client.createMockOrFallbackStatus(status.errorMessage)
            } else {
                _hotspotStatus.value = status
            }
            _isLoading.value = false
        }
    }

    fun testPing() {
        viewModelScope.launch {
            val gw = _networkInfo.value.gatewayIp
            val ping = NetworkUtils.pingGateway(gw)
            _networkInfo.value = _networkInfo.value.copy(
                pingLatencyMs = ping,
                isGatewayReachable = ping != null
            )
            _actionMessage.value = if (ping != null) {
                "تم الاتصال بالسيرفر بنجاح! سرعة الاستجابة: $ping مللي ثانية"
            } else {
                "تعذر عمل Ping للسيرفر ($gw). تأكد من الاتصال بالواي فاي"
            }
        }
    }

    fun setGatewayIp(newIp: String) {
        val clean = newIp.trim().removePrefix("http://").removePrefix("https://").substringBefore("/")
        _customGateway.value = clean
        detectNetwork()
        refreshStatus()
        _actionMessage.value = "تم تغيير عنوان البوابة إلى: $clean"
    }

    fun setTargetQuota(quota: Double) {
        if (quota > 0) {
            _userTargetQuotaGb.value = quota
        }
    }

    fun toggleDemoMode() {
        _isDemoMode.value = !_isDemoMode.value
        refreshStatus()
        _actionMessage.value = if (_isDemoMode.value) "تم تفعيل وضع التجربة (عرض حي للجيجات)" else "تم التبديل للاتصال المباشر بالسيرفر"
    }

    fun selectTab(tab: HotspotTab) {
        _activeTab.value = tab
    }

    fun clearActionMessage() {
        _actionMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            val success = client.performLogout(_networkInfo.value.gatewayIp)
            _actionMessage.value = if (success) "تم تسجيل الخروج من السيرفر بنجاح" else "تم إرسال طلب تسجيل الخروج"
            refreshStatus()
        }
    }

    fun addCard(code: String, pass: String, quota: Double, price: Double, notes: String) {
        viewModelScope.launch {
            cardDao.insertCard(
                CardEntity(
                    cardCode = code.trim(),
                    password = pass.trim(),
                    quotaGb = quota,
                    priceEgp = price,
                    notes = notes.trim()
                )
            )
            _actionMessage.value = "تم حفظ الكارت بنجاح في المحفظة"
        }
    }

    fun toggleCardUsed(card: CardEntity) {
        viewModelScope.launch {
            cardDao.updateCard(card.copy(isUsed = !card.isUsed))
        }
    }

    fun deleteCard(card: CardEntity) {
        viewModelScope.launch {
            cardDao.deleteCard(card)
            _actionMessage.value = "تم حذف الكارت"
        }
    }

    fun getHotspotStatusUrl(): String {
        val gw = _networkInfo.value.gatewayIp
        return if (gw.startsWith("http://") || gw.startsWith("https://")) "$gw/status" else "http://$gw/status"
    }

    fun getHotspotLoginUrl(): String {
        val gw = _networkInfo.value.gatewayIp
        return if (gw.startsWith("http://") || gw.startsWith("https://")) "$gw/login" else "http://$gw/login"
    }

    fun getHotspotLogoutUrl(): String {
        val gw = _networkInfo.value.gatewayIp
        return if (gw.startsWith("http://") || gw.startsWith("https://")) "$gw/logout" else "http://$gw/logout"
    }
}
