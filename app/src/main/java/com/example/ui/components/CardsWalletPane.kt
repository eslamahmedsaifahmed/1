package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CardEntity
import com.example.ui.theme.DoctorAccentAmber
import com.example.ui.theme.DoctorAccentGreen
import com.example.ui.theme.DoctorPrimary
import com.example.ui.theme.DoctorSecondary

@Composable
fun CardsWalletPane(
    cards: List<CardEntity>,
    onAddNewCard: () -> Unit,
    onCopyCode: (String) -> Unit,
    onLoginWithCard: (CardEntity) -> Unit,
    onToggleUsed: (CardEntity) -> Unit,
    onDeleteCard: (CardEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSavedGb = cards.filter { !it.isUsed }.sumOf { it.quotaGb }
    val activeCount = cards.count { !it.isUsed }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cards_wallet_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.linearGradient(
                            listOf(DoctorPrimary.copy(alpha = 0.4f), DoctorSecondary.copy(alpha = 0.2f))
                        )
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "محفظة كروت شبكة el.doctor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "احفظ كروتك واشحنها بلمسة واحدة",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$activeCount كروت نشطة",
                                style = MaterialTheme.typography.labelSmall,
                                color = DoctorSecondary,
                                fontWeight = FontWeight.Bold
                            )
                            if (totalSavedGb > 0) {
                                Text(
                                    text = "$totalSavedGb جيجا",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = DoctorPrimary
                                )
                            }
                        }
                    }
                }
            }

            if (cards.isEmpty()) {
                item {
                    EmptyCardsPlaceholder(onAddNewCard)
                }
            } else {
                items(cards, key = { it.id }) { card ->
                    CardVoucherItem(
                        card = card,
                        onCopyCode = { onCopyCode(card.cardCode) },
                        onLoginWithCard = { onLoginWithCard(card) },
                        onToggleUsed = { onToggleUsed(card) },
                        onDelete = { onDeleteCard(card) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // FAB to add card
        FloatingActionButton(
            onClick = onAddNewCard,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_card_fab"),
            containerColor = DoctorPrimary,
            contentColor = Color(0xFF00202E)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة كارت جديد")
        }
    }
}

@Composable
fun CardVoucherItem(
    card: CardEntity,
    onCopyCode: () -> Unit,
    onLoginWithCard: () -> Unit,
    onToggleUsed: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_voucher_${card.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isUsed)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (card.isUsed) 1.dp else 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (card.isUsed) Color.Gray.copy(alpha = 0.2f)
                                else DoctorPrimary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = if (card.isUsed) Color.Gray else DoctorPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (card.quotaGb > 0) "كرت ${card.quotaGb.toInt()} جيجابايت" else "كرت إنترنت",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (card.isUsed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        if (card.priceEgp > 0) {
                            Text(
                                text = "السعر: ${card.priceEgp.toInt()} جنيه",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (card.isUsed) Color.Gray.copy(alpha = 0.15f)
                            else DoctorAccentGreen.copy(alpha = 0.15f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (card.isUsed) "مستهلك" else "جاهز للاستخدام",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.isUsed) Color.Gray else DoctorAccentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Code Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "كود الكارت (Username)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = card.cardCode,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = if (card.isUsed) Color.Gray else DoctorPrimary
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onCopyCode,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "نسخ الكود",
                                tint = DoctorSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            if (card.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ملاحظة: ${card.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onLoginWithCard,
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFF00202E)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "دخول بهذا الكارت",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00202E)
                        )
                    }

                    OutlinedButton(
                        onClick = onToggleUsed,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (card.isUsed) "تنشيط" else "تحديد كمستهلك",
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "حذف الكارت",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCardsPlaceholder(onAddNewCard: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(DoctorPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = null,
                    tint = DoctorPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "لا توجد كروت محفوظة حالياً",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "احفظ كروت شبكة el.doctor هنا حتى لا تفقد الأكواد، واستخدمها لتسجيل الدخول السريع في أي وقت.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddNewCard,
                colors = ButtonDefaults.buttonColors(containerColor = DoctorPrimary)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF00202E))
                Spacer(modifier = Modifier.width(6.dp))
                Text("إضافة كرت الآن", color = Color(0xFF00202E), fontWeight = FontWeight.Bold)
            }
        }
    }
}
