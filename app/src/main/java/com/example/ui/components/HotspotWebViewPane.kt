package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DoctorAccentAmber
import com.example.ui.theme.DoctorAccentRed
import com.example.ui.theme.DoctorPrimary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HotspotWebViewPane(
    initialUrl: String,
    gatewayIp: String,
    onStatusDetected: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var pageTitle by remember { mutableStateOf("جاري التحميل...") }
    var webProgress by remember { mutableIntStateOf(0) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val statusUrl = "http://$gatewayIp/status"
    val loginUrl = "http://$gatewayIp/login"
    val logoutUrl = "http://$gatewayIp/logout"

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("hotspot_webview_pane")
    ) {
        // Navigation toolbar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoBack() == true) {
                                webViewInstance?.goBack()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "السابق",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            if (webViewInstance?.canGoForward() == true) {
                                webViewInstance?.goForward()
                            }
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "التالي",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = {
                            hasError = false
                            webViewInstance?.reload()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "إعادة تحميل",
                            tint = DoctorPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Address bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentUrl,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Quick Destination Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = currentUrl.contains("status"),
                        onClick = {
                            hasError = false
                            currentUrl = statusUrl
                            webViewInstance?.loadUrl(statusUrl)
                        },
                        label = { Text("صفحة Status", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Info, null, modifier = Modifier.size(14.dp), tint = DoctorPrimary)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DoctorPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = DoctorPrimary
                        )
                    )

                    FilterChip(
                        selected = currentUrl.contains("login"),
                        onClick = {
                            hasError = false
                            currentUrl = loginUrl
                            webViewInstance?.loadUrl(loginUrl)
                        },
                        label = { Text("صفحة Login", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LockOpen, null, modifier = Modifier.size(14.dp), tint = Color(0xFF00E676))
                        }
                    )

                    FilterChip(
                        selected = currentUrl.contains("logout"),
                        onClick = {
                            hasError = false
                            currentUrl = logoutUrl
                            webViewInstance?.loadUrl(logoutUrl)
                        },
                        label = { Text("Logout", fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.PowerSettingsNew, null, modifier = Modifier.size(14.dp), tint = DoctorAccentRed)
                        }
                    )
                }
            }
        }

        // Loading bar
        if (webProgress in 1..99) {
            LinearProgressIndicator(
                progress = { webProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = DoctorPrimary
            )
        }

        // Web view or friendly fallback when router isn't physically on current network
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_NO_CACHE
                            builtInZoomControls = true
                            displayZoomControls = false
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                webProgress = newProgress
                            }

                            override fun onReceivedTitle(view: WebView?, title: String?) {
                                pageTitle = title ?: ""
                            }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                hasError = false
                                url?.let { currentUrl = it }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                url?.let { currentUrl = it }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                if (request?.isForMainFrame == true) {
                                    hasError = true
                                    errorMessage = "تعذر الاتصال بـ $currentUrl. تأكد من اتصال هاتفك بشبكة واي فاي el.doctor."
                                }
                            }
                        }

                        loadUrl(initialUrl)
                        webViewInstance = this
                    }
                },
                update = { webView ->
                    webViewInstance = webView
                },
                modifier = Modifier.fillMaxSize()
            )

            // If local MikroTik router cannot be reached
            if (hasError) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.96f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DoctorAccentAmber.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = DoctorAccentAmber,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "تعذر تحميل صفحة المايكروتك",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "العنوان المطلوب: $currentUrl\n\nتأكد من أن هاتفك متصل بشبكة الواي فاي التابعة لسيرفر el.doctor (10.0.0.1 أو 192.168.88.1).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    hasError = false
                                    webViewInstance?.loadUrl(statusUrl)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary)
                            ) {
                                Text("إعادة المحاولة (Status)", color = Color(0xFF00202E), fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    hasError = false
                                    webViewInstance?.loadUrl(loginUrl)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text("فتح صفحة الدخول", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }
}
