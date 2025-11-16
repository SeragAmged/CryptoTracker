package com.serag.cryptotracker.crypto.presintation.coins_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serag.cryptotracker.core.domain.util.onError
import com.serag.cryptotracker.core.domain.util.onResult
import com.serag.cryptotracker.crypto.data.CryptoRepository
import com.serag.cryptotracker.crypto.presintation.coin_detail.toDataPoint
import com.serag.cryptotracker.crypto.presintation.models.CoinUi
import com.serag.cryptotracker.crypto.presintation.models.toCoinUi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class CoinsListViewModel(
    private val cryptoRepository: CryptoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsListState())
    val state = _state
        .onStart { loadCoins() }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            CoinsListState()
        )

    private val _events = Channel<CoinsListEvent>()
    val events = _events.receiveAsFlow()
    fun onAction(action: CoinsLisActions) {
        when (action) {
            CoinsLisActions.RefreshCoinsList -> loadCoins(isRefresh = true)
            is CoinsLisActions.OnSearch -> onSearch(action.statement)
            is CoinsLisActions.OnCoinClick -> selectCoin(action.coinUi)
            is CoinsLisActions.RefreshSelectedCoinPrices -> loadCoinPriceHistory(true)
        }
    }

    private fun onSearch(statement: String) {
        _state.update {
            it.copy(
                coins = cryptoRepository.searchCoin(statement).map { it -> it.toCoinUi() }
            )
        }

    }

    private fun selectCoin(coinUi: CoinUi) {
        _state.update { it.copy(selectedCoin = coinUi) }
        loadCoinPriceHistory()
    }

    private fun loadCoinPriceHistory(isRefresh: Boolean = false) {
        _state.update {
            it.copy(
                isRefreshing = isRefresh,
                selectedCoin = it.selectedCoin?.copy(priceHistory = emptyList())
            )
        }
        val coinUi = _state.value.selectedCoin
        if (coinUi != null)
            viewModelScope.launch {
                cryptoRepository.getCoinHistory(
                    coinId = coinUi.id,
                    start = ZonedDateTime.now().minusDays(5),
                    end = ZonedDateTime.now()
                ).onResult { error, coinPrices ->
                    delay(500)
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            selectedCoin = it.selectedCoin?.copy(
                                priceHistory = coinPrices?.sortedBy { it -> it.dateTime }
                                    ?.map { it -> it.toDataPoint() }
                                    ?: emptyList()
                            )
                        )
                    }
                }.onError { error, prices -> _events.send(CoinsListEvent.Error(error)) }
            }
    }

    private fun loadCoins(isRefresh: Boolean = false) {
        _state.update {
            if (isRefresh) it.copy(isRefreshing = true)
            else it.copy(isLoading = true)
        }
        viewModelScope.launch {
            cryptoRepository.getCoins()
                .onResult { error, coins ->
                    delay(500)
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            coins = coins?.map { coin -> coin.toCoinUi() } ?: emptyList()
                        )
                    }
                }.onError { error, coins ->
                    _events.send(CoinsListEvent.Error(error))
                }
        }

    }
}