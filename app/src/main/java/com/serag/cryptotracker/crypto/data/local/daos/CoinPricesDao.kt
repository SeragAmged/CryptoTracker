package com.serag.cryptotracker.crypto.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinPriceDto

@Dao
interface CoinPricesDao {

    @Insert
    suspend fun addCoinPrices(coinPricesDto: List<LocalCoinPriceDto>)

    @Query("SELECT * FROM coin_prices WHERE coinId = :coinId")
    suspend fun getCoinPrices(coinId: String): List<LocalCoinPriceDto>
}