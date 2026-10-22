package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HotspotTab
import com.example.ui.components.AddCardDialog
import com.example.ui.components.CardsWalletPane
import com.example.ui.components.DoctorHeader
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.HotspotWebViewPane
import com.example.ui.components.NetworkToolsPane
import com.example.ui.components.QuickShortcutsRow
import com.example.ui.components.QuotaDashboardCard
import com.example.ui.components.ServerSettingsDialog
import com.example.ui.components.SupportDialog
import com.example.ui.theme.DoctorPrimary
import com.example.ui.theme.DoctorSecondary
import com.example.ui.viewmodel.HotspotViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HotspotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val status by viewModel.hotspotStatus.collectAsStateWithLifecycle()
    val networkInfo by viewModel.networkInfo.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val customGateway by viewModel.customGateway.collectAsStateWithLifecycle()
    val targetQuotaGb by viewModel.userTargetQuotaGb.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isDemoMode.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()
    val savedCards by viewModel.savedCards.collectAsStateWithLifecycle()

    var showAddCardDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }

    // Handle back button when on sub-tabs
    BackHandler(enabled = activeTab != HotspotTab.DASHBOARD) {
        viewModel.selectTab(HotspotTab.DASHBOARD)
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearActionMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DoctorHeader(
                networkInfo = networkInfo,
                isLoading = isLoading,
                isDemoMode = isDemoMode,
                onRefresh = { viewModel.refreshStatus() },
                onOpenSettings = { showSettingsDialog = true },
                onOpenAbout = { showSupportDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == HotspotTab.DASHBOARD,
                    onClick = { viewModel.selectTab(HotspotTab.DASHBOARD) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "الرئيسية والجيجات") },
                    label = { Text("الجيجات", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DoctorPrimary,
                        selectedTextColor = DoctorPrimary,
                        indicatorColor = DoctorPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_dashboard")
                )

                NavigationBarItem(
                    selected = activeTab == HotspotTab.HOTSPOT_WEBVIEW,
                    onClick = { viewModel.selectTab(HotspotTab.HOTSPOT_WEBVIEW) },
                    icon = { Icon(Icons.Default.Language, contentDescription = "صفحة Status") },
                    label = { Text("صفحة Status", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DoctorPrimary,
                        selectedTextColor = DoctorPrimary,
                        indicatorColor = DoctorPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_webview")
                )

                NavigationBarItem(
                    selected = activeTab == HotspotTab.CARDS_WALLET,
                    onClick = { viewModel.selectTab(HotspotTab.CARDS_WALLET) },
                    icon = { Icon(Icons.Default.CreditCard, contentDescription = "كروت النت") },
                    label = { Text("كروت النت", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DoctorSecondary,
                        selectedTextColor = DoctorSecondary,
                        indicatorColor = DoctorSecondary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_cards")
                )

                NavigationBarItem(
                    selected = activeTab == HotspotTab.DIAGNOSTICS,
                    onClick = { viewModel.selectTab(HotspotTab.DIAGNOSTICS) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "أدوات ودعم") },
                    label = { Text("أدوات الشبكة", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DoctorPrimary,
                        selectedTextColor = DoctorPrimary,
                        indicatorColor = DoctorPrimary.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier.testTag("tab_tools")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                HotspotTab.DASHBOARD -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .testTag("dashboard_content")
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            HeroBannerCard(
                                networkInfo = networkInfo,
                                onTestPing = { viewModel.testPing() }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            QuickShortcutsRow(
                                onOpenStatusPage = { viewModel.selectTab(HotspotTab.HOTSPOT_WEBVIEW) },
                                onOpenLoginPage = { viewModel.selectTab(HotspotTab.HOTSPOT_WEBVIEW) },
                                onLogout = { viewModel.logout() },
                                onTestPing = { viewModel.testPing() },
                                onOpenExternalBrowser = {
                                    val statusUrl = viewModel.getHotspotStatusUrl()
                                    openExternalUrl(context, statusUrl)
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            QuotaDashboardCard(
                                status = status,
                                targetQuotaGb = targetQuotaGb,
                                onTargetQuotaChange = { viewModel.setTargetQuota(it) },
                                onLogoutClick = { viewModel.logout() }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }

                HotspotTab.HOTSPOT_WEBVIEW -> {
                    HotspotWebViewPane(
                        initialUrl = viewModel.getHotspotStatusUrl(),
                        gatewayIp = networkInfo.gatewayIp
                    )
                }

                HotspotTab.CARDS_WALLET -> {
                    CardsWalletPane(
                        cards = savedCards,
                        onAddNewCard = { showAddCardDialog = true },
                        onCopyCode = { code ->
                            copyToClipboard(context, code)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("تم نسخ كود الكارت: $code")
                            }
                        },
                        onLoginWithCard = { card ->
                            copyToClipboard(context, card.cardCode)
                            viewModel.selectTab(HotspotTab.HOTSPOT_WEBVIEW)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("تم نسخ الكود، جاري فتح صفحة الدخول...")
                            }
                        },
                        onToggleUsed = { card -> viewModel.toggleCardUsed(card) },
                        onDeleteCard = { card -> viewModel.deleteCard(card) }
                    )
                }

                HotspotTab.DIAGNOSTICS -> {
                    NetworkToolsPane(
                        networkInfo = networkInfo,
                        onTestPing = { viewModel.testPing() },
                        onOpenSettings = { showSettingsDialog = true },
                        onContactSupport = { showSupportDialog = true },
                        onOpenExternalBrowser = {
                            openExternalUrl(context, viewModel.getHotspotStatusUrl())
                        }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddCardDialog) {
        AddCardDialog(
            onDismiss = { showAddCardDialog = false },
            onSave = { code, pass, quota, price, notes ->
                viewModel.addCard(code, pass, quota, price, notes)
            }
        )
    }

    if (showSettingsDialog) {
        ServerSettingsDialog(
            currentGateway = customGateway,
            isDemoMode = isDemoMode,
            onSaveGateway = { viewModel.setGatewayIp(it) },
            onToggleDemoMode = { viewModel.toggleDemoMode() },
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showSupportDialog) {
        SupportDialog(onDismiss = { showSupportDialog = false })
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Card Code", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "تم نسخ الكود: $text", Toast.LENGTH_SHORT).show()
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "تعذر فتح المتصفح الخارجي", Toast.LENGTH_SHORT).show()
    }
}
