package com.serag.cryptotracker.crypto.data.remote

import com.serag.cryptotracker.core.data.networking.safeCall
import com.serag.cryptotracker.core.domain.util.NetworkError
import com.serag.cryptotracker.core.domain.util.Result
import com.serag.cryptotracker.core.domain.util.map
import com.serag.cryptotracker.crypto.data.mappers.toCoin
import com.serag.cryptotracker.crypto.data.mappers.toCoinPrice
import com.serag.cryptotracker.crypto.data.remote.dtos.CoinHistoryDto
import com.serag.cryptotracker.crypto.data.remote.dtos.CoinsResponseDto
import com.serag.cryptotracker.crypto.domain.Coin
import com.serag.cryptotracker.crypto.domain.CoinPrice
import com.serag.cryptotracker.crypto.domain.CoinsDataSource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import java.time.ZonedDateTime

class RemoteCoinsDataSource(private val client: HttpClient) : CoinsDataSource {
    override suspend fun getCoins(): Result<List<Coin>, NetworkError> = safeCall<CoinsResponseDto> {
        client.get("assets")
    }.map { it?.data?.map { dto -> dto.toCoin() } ?: emptyList() }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, NetworkError> =
        safeCall<CoinHistoryDto> {
            client.get("assets/$coinId/history?interval=h6") {
                parameter("start", start.toInstant().toEpochMilli())
                parameter("end", end.toInstant().toEpochMilli())
            }
        }.map { it?.data?.map { dto -> dto.toCoinPrice() } ?: emptyList() }

}