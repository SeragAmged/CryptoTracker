package com.serag.cryptotracker.crypto.presintation.coins_list

import androidx.compose.runtime.Immutable
import com.serag.cryptotracker.crypto.presintation.models.CoinUi

@Immutable
data class CoinsListState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val coins: List<CoinUi> = emptyList(),
    val selectedCoin: CoinUi? = null
)
