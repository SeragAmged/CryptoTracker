package com.serag.cryptotracker.crypto.data.mappers

import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinDto
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinPriceDto
import com.serag.cryptotracker.crypto.data.remote.dtos.CoinDto
import com.serag.cryptotracker.crypto.data.remote.dtos.CoinPriceDto
import com.serag.cryptotracker.crypto.domain.Coin
import com.serag.cryptotracker.crypto.domain.CoinPrice
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun CoinDto.toCoin() = Coin(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun CoinPriceDto.toCoinPrice() = CoinPrice(
    priceUsd = priceUsd,
    dateTime = time.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(it),
            ZoneOffset.systemDefault()
        )
    }
)

fun LocalCoinDto.toCoin(): Coin = Coin(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun LocalCoinPriceDto.toCoinPrice(): CoinPrice = CoinPrice(
    priceUsd = priceUsd,
    dateTime = time.let {
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(it),
            ZoneOffset.systemDefault()
        )
    }
)

fun CoinDto.toLocalCoin() = LocalCoinDto(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    marketCapUsd = marketCapUsd,
    priceUsd = priceUsd,
    changePercent24Hr = changePercent24Hr
)

fun CoinPriceDto.toLocalCoinPrice(id: String) = LocalCoinPriceDto(
    coinId = id,
    priceUsd = priceUsd,
    time = time
)