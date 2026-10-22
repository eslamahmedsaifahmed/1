package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DoctorPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServerSettingsDialog(
    currentGateway: String,
    isDemoMode: Boolean,
    onSaveGateway: (String) -> Unit,
    onToggleDemoMode: () -> Unit,
    onDismiss: () -> Unit
) {
    var gatewayInput by remember { mutableStateOf(currentGateway) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إعدادات سيرفر المايكروتك",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "عنوان الـ IP الافتراضي لصفحة الهوت سبوت (Status / Login):",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = gatewayInput,
                    onValueChange = { gatewayInput = it },
                    label = { Text("عنوان السيرفر أو البوابة") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gateway_ip_input")
                )

                Text(
                    text = "عناوين شائعة لسيرفرات مايكروتك:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("10.0.0.1", "192.168.88.1", "172.16.0.1", "eldoctor.hotspot", "aldoctor.net").forEach { preset ->
                        FilterChip(
                            selected = gatewayInput == preset,
                            onClick = { gatewayInput = preset },
                            label = { Text(preset, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Demo Mode Toggle (great for testing or showcasing without physical hotspot router)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "وضع العرض الحي (تجريبي)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "توليد بيانات حية للجيجات والاستهلاك لتجربة التطبيق بدون راوتر متصل",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isDemoMode,
                        onCheckedChange = { onToggleDemoMode() },
                        colors = SwitchDefaults.colors(checkedThumbColor = DoctorPrimary)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (gatewayInput.isNotBlank()) {
                        onSaveGateway(gatewayInput.trim())
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary),
                modifier = Modifier.testTag("save_gateway_button")
            ) {
                Text("حفظ التغييرات", color = Color(0xFF00202E), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
