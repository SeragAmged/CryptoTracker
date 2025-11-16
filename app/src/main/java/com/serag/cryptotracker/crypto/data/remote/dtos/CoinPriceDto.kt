package com.serag.cryptotracker.crypto.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinPriceDto(
    val priceUsd: Double,
    val time: Long
)
