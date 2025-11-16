package com.serag.cryptotracker.crypto.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinHistoryDto(
    val data: List<CoinPriceDto>
)
