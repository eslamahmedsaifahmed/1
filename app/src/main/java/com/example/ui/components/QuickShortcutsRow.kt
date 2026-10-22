package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DoctorAccentAmber
import com.example.ui.theme.DoctorAccentGreen
import com.example.ui.theme.DoctorAccentRed
import com.example.ui.theme.DoctorPrimary
import com.example.ui.theme.DoctorSecondary

@Composable
fun QuickShortcutsRow(
    onOpenStatusPage: () -> Unit,
    onOpenLoginPage: () -> Unit,
    onLogout: () -> Unit,
    onTestPing: () -> Unit,
    onOpenExternalBrowser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_shortcuts_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "اختصارات سريعة للمايكروتك",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ShortcutItem(
                    label = "صفحة Status",
                    sub = "الحالة",
                    icon = Icons.Default.Info,
                    color = DoctorPrimary,
                    tag = "shortcut_status",
                    onClick = onOpenStatusPage
                )

                ShortcutItem(
                    label = "تسجيل دخول",
                    sub = "Login",
                    icon = Icons.Default.LockOpen,
                    color = DoctorAccentGreen,
                    tag = "shortcut_login",
                    onClick = onOpenLoginPage
                )

                ShortcutItem(
                    label = "تسجيل خروج",
                    sub = "Logout",
                    icon = Icons.Default.PowerSettingsNew,
                    color = DoctorAccentRed,
                    tag = "shortcut_logout",
                    onClick = onLogout
                )

                ShortcutItem(
                    label = "فحص Ping",
                    sub = "السرعة",
                    icon = Icons.Default.Speed,
                    color = DoctorSecondary,
                    tag = "shortcut_ping",
                    onClick = onTestPing
                )

                ShortcutItem(
                    label = "متصفح خارجي",
                    sub = "Chrome",
                    icon = Icons.Default.OpenInBrowser,
                    color = DoctorAccentAmber,
                    tag = "shortcut_external",
                    onClick = onOpenExternalBrowser
                )
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    label: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag(tag)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = sub,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
