package com.serag.cryptotracker.core.di


import androidx.room.Room
import com.serag.cryptotracker.core.data.networking.HttpClientFactory
import com.serag.cryptotracker.crypto.data.CryptoRepository
import com.serag.cryptotracker.crypto.data.local.CoinsDataBase
import com.serag.cryptotracker.crypto.data.local.LocalCoinsDataSource
import com.serag.cryptotracker.crypto.data.remote.RemoteCoinsDataSource
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsListViewModel
import io.ktor.client.engine.cio.CIO
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    //HttpClient
    single { HttpClientFactory.create(CIO.create()) }

    // database
    single {
        Room.databaseBuilder(
            get(),
            CoinsDataBase::class.java,
            "coins_db"
        ).build()
    }

    single { get<CoinsDataBase>().coinsDao }
    single { get<CoinsDataBase>().coinPricesDao }

    // data sources
    single { RemoteCoinsDataSource(get()) }
    single { LocalCoinsDataSource(get(), get()) }


    // repository
    single { CryptoRepository(get(), get()) }

    // viewmodel
    viewModelOf(::CoinsListViewModel)
}
