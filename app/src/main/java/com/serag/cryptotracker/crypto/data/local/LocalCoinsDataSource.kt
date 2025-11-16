package com.serag.cryptotracker.crypto.data.local

import android.util.Log
import com.serag.cryptotracker.core.domain.util.DatabaseError
import com.serag.cryptotracker.core.domain.util.Error
import com.serag.cryptotracker.core.domain.util.Result
import com.serag.cryptotracker.crypto.data.local.daos.CoinPricesDao
import com.serag.cryptotracker.crypto.data.local.daos.CoinsDao
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinDto
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinPriceDto
import com.serag.cryptotracker.crypto.data.mappers.toCoin
import com.serag.cryptotracker.crypto.data.mappers.toCoinPrice
import com.serag.cryptotracker.crypto.domain.Coin
import com.serag.cryptotracker.crypto.domain.CoinPrice
import com.serag.cryptotracker.crypto.domain.CoinsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime


class LocalCoinsDataSource(
    private val coinsDao: CoinsDao,
    private val coinPricesDao: CoinPricesDao,
) : CoinsDataSource {
    override suspend fun getCoins(): Result<List<Coin>, Error> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(coinsDao.getCoins().map { it.toCoin() })

            } catch (_: Exception) {
                Result.Error(DatabaseError.UNKNOWN)
            }
        }

    override suspend fun getCoinHistory(
        coinId: String,
        start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, Error> =
        withContext(Dispatchers.IO) {
            try {
                Result.Success(coinPricesDao.getCoinPrices(coinId).map {
                    Log.d("DataBase", it.toString())
                    it.toCoinPrice()
                })
            } catch (_: Exception) {
                Result.Error(DatabaseError.UNKNOWN)
            }
        }

    suspend fun saveCoins(coins: List<Coin>) = withContext(Dispatchers.IO) {

        try {
            val coinsDto = coins.map {
                LocalCoinDto(
                    id = it.id,
                    rank = it.rank,
                    name = it.name,
                    symbol = it.symbol,
                    marketCapUsd = it.marketCapUsd,
                    priceUsd = it.priceUsd,
                    changePercent24Hr = it.changePercent24Hr
                )
            }
            coinsDao.addCoins(coinsDto)
        } catch (e: Exception) {
            Log.e("DataBase", "saveCoins error : $e")
        }

    }

    suspend fun saveCoinPrices(
        coinId: String,
        coinPrices: List<CoinPrice>
    ) = withContext(Dispatchers.IO) {

        try {
            val pricesDto = coinPrices.map {
                LocalCoinPriceDto(
                    coinId = coinId,
                    priceUsd = it.priceUsd,
                    time = it.dateTime.toInstant().toEpochMilli()
                )
            }
            coinPricesDao.addCoinPrices(pricesDto)

        } catch (e: Exception) {
            Log.e("DataBase", "saveCoins error : $e")
        }
    }
}