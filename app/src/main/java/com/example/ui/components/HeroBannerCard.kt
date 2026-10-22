package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.NetworkGatewayInfo
import com.example.ui.theme.DoctorAccentGreen
import com.example.ui.theme.DoctorPrimary
import com.example.ui.theme.DoctorSecondary

@Composable
fun HeroBannerCard(
    networkInfo: NetworkGatewayInfo,
    onTestPing: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            // Background High-Tech Art Banner
            Image(
                painter = painterResource(id = R.drawable.img_eldoctor_banner),
                contentDescription = "el.doctor Hotspot Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            // Cyber Gradient Overlay for text readability
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x77070D1B),
                                Color(0xEE070D1B)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top status pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF070D1B).copy(alpha = 0.8f))
                            .border(1.dp, DoctorPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(DoctorAccentGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "سيرفر مايكروتك أونلاين",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = DoctorPrimary
                            )
                        }
                    }

                    if (networkInfo.pingLatencyMs != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF070D1B).copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = DoctorSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${networkInfo.pingLatencyMs} ms",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DoctorSecondary
                                )
                            }
                        }
                    }
                }

                // Middle & Bottom Branding
                Column {
                    Text(
                        text = "شبكة el.doctor",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "سرعة فائقة • استقرار تام • تحكم كامل في رصيد الجيجات",
                        style = MaterialTheme.typography.bodySmall,
                        color = DoctorPrimary.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}
