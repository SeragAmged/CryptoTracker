package com.serag.cryptotracker.crypto.data.local.dtos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("coins")
data class LocalCoinDto(
    @PrimaryKey
    @ColumnInfo(index = true)
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val marketCapUsd: Double,
    val priceUsd: Double,
    val changePercent24Hr: Double,
)
