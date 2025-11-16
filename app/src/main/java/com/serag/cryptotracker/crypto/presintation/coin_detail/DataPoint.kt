package com.serag.cryptotracker.crypto.presintation.coin_detail

import com.serag.cryptotracker.crypto.domain.CoinPrice
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class DataPoint(
    val x: Float,
    val y: Float,
    val xLapel: String
)

fun CoinPrice.toDataPoint(): DataPoint {
    return DataPoint(
        x = dateTime.toEpochSecond().toFloat(),
        y = priceUsd.toFloat(),
        xLapel = dateTime.formatToDataPointLapel()
    )
}

fun ZonedDateTime.formatToDataPointLapel(): String {
    return DateTimeFormatter.ofPattern("ha\nM/d").format(this)
}