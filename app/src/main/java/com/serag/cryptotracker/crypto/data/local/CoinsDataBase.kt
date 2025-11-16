package com.serag.cryptotracker.crypto.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.serag.cryptotracker.crypto.data.local.daos.CoinPricesDao
import com.serag.cryptotracker.crypto.data.local.daos.CoinsDao
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinDto
import com.serag.cryptotracker.crypto.data.local.dtos.LocalCoinPriceDto


@Database(
    entities = [LocalCoinDto::class, LocalCoinPriceDto::class],
    version = 1,
    exportSchema = false,
)
abstract class CoinsDataBase() : RoomDatabase() {
    abstract val coinsDao: CoinsDao
    abstract val coinPricesDao: CoinPricesDao
}