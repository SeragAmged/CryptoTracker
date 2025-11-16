package com.serag.cryptotracker.crypto.data.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class CoinsResponseDto(
    val data: List<CoinDto>
)