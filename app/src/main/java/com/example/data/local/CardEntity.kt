package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotspot_cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardCode: String,
    val password: String = "",
    val quotaGb: Double = 0.0,
    val priceEgp: Double = 0.0,
    val notes: String = "",
    val isUsed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
