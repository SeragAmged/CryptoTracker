package com.serag.cryptotracker.crypto.data.local.dtos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    "coin_prices",
    foreignKeys = [ForeignKey(
        entity = LocalCoinDto::class,
        parentColumns = ["id"],
        childColumns = ["coinId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("coinId")]
)
data class LocalCoinPriceDto(
    @PrimaryKey(autoGenerate = true)
    val pk: Long = 0,
    val coinId: String,
    val priceUsd: Double,
    val time: Long
)
