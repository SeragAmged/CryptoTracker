package com.serag.cryptotracker.crypto.presintation.coins_list


sealed class CoinsListEvent {
    data class Error(val error: com.serag.cryptotracker.core.domain.util.Error) : CoinsListEvent()

}