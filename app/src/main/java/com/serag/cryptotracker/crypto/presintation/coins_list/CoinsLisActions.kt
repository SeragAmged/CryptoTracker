package com.serag.cryptotracker.crypto.presintation.coins_list

import com.serag.cryptotracker.crypto.presintation.models.CoinUi

sealed class CoinsLisActions {
    data class OnCoinClick(val coinUi: CoinUi) : CoinsLisActions()
    data class OnSearch(val statement: String) : CoinsLisActions()
    object RefreshCoinsList : CoinsLisActions()
    object RefreshSelectedCoinPrices : CoinsLisActions()
}