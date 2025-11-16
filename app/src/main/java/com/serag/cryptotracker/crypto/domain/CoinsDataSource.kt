package com.serag.cryptotracker.crypto.domain

import com.serag.cryptotracker.core.domain.util.Error
import com.serag.cryptotracker.core.domain.util.Result
import java.time.ZonedDateTime

interface CoinsDataSource {
    suspend fun getCoins(): Result<List<Coin>, Error>
    suspend fun getCoinHistory(
        coinId: String,start: ZonedDateTime,
        end: ZonedDateTime
    ): Result<List<CoinPrice>, Error>

}