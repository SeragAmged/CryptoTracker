package com.serag.cryptotracker.core.navigation

import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.serag.cryptotracker.core.presintation.util.ObserveAsEvents
import com.serag.cryptotracker.core.presintation.util.toString
import com.serag.cryptotracker.crypto.presintation.coin_detail.CoinDetailsScreen
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinListScreen
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsLisActions
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsListEvent
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsListViewModel
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun adaptiveListCoinDetailName(
    viewModel: CoinsListViewModel = koinViewModel(),
    modifier: Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ObserveAsEvents(
        viewModel.events, onEvent = { event ->
            when (event) {
                is CoinsListEvent.Error -> Toast.makeText(
                    context, event.error.toString(context), LENGTH_LONG
                ).show()
            }

        })

    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    NavigableListDetailPaneScaffold(
        navigator,
        {
            CoinListScreen(
                state = state,
                onAction = { action ->
                    viewModel.onAction(action)
                    when (action) {
                        is CoinsLisActions.OnCoinClick -> navigator.navigateTo(
                            pane = ThreePaneScaffoldRole.Primary
                        )

                        else -> {}
                    }
                },
            )
        },
        { CoinDetailsScreen(state, viewModel::onAction) },
        modifier,
    )

}