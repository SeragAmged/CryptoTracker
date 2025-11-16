package com.serag.cryptotracker.crypto.data.local.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinDto

@Dao
interface CoinsDao {
    @Query("SELECT * FROM coins")
    suspend fun getCoins(): List<LocalCoinDto>

    @Upsert()
    suspend fun addCoins(coins: List<LocalCoinDto>)
}