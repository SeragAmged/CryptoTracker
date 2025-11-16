package com.serag.cryptotracker.crypto.data

import com.serag.cryptotracker.core.domain.util.Error
import com.serag.cryptotracker.core.domain.util.Result
import com.serag.cryptotracker.core.domain.util.onError
import com.serag.cryptotracker.core.domain.util.onSuccess
import com.serag.cryptotracker.crypto.data.local.LocalCoinsDataSource
import com.serag.cryptotracker.crypto.data.remote.RemoteCoinsDataSource
import com.serag.cryptotracker.crypto.domain.Coin
import com.serag.cryptotracker.crypto.domain.CoinPrice
import com.serag.cryptotracker.crypto.domain.CoinsDataSource
import java.time.ZonedDateTime


class CryptoRepository(
    private val remote: RemoteCoinsDataSource,
    private val local: LocalCoinsDataSource,
) : CoinsDataSource {

    private var coins: List<Coin> = emptyList()
    override suspend fun getCoins(): Result<List<Coin>, Error> {
        return remote.getCoins()
            .onSuccess {
                local.saveCoins(it)
                coins = it
            }.onError { error, coins ->
                val result = local.getCoins()
                return if (result is Result.Success)
                    Result.Error(error, result.data)
                else result
            }
    }

    fun searchCoin(query: String): List<Coin> {
        val trimmedQuery = query.trim().lowercase()
        if (trimmedQuery.isEmpty()) return coins
        return coins.filter { coin ->
            coin.name.lowercase().contains(trimmedQuery) ||
                    coin.symbol.lowercase().contains(trimmedQuery)
        }
    }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, Error> {
        return remote.getCoinHistory(coinId, start, end)
            .onSuccess { local.saveCoinPrices(coinId, it) }.onError { error, coins ->
                val result = local.getCoinHistory(coinId, start, end)
                return if (result is Result.Success)
                    Result.Error(error, result.data)
                else result
            }

    }
}