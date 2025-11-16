package com.serag.cryptotracker.crypto.presintation.coins_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.serag.cryptotracker.crypto.presintation.coins_list.components.CoinListItem
import com.serag.cryptotracker.crypto.presintation.coins_list.components.ExpandableSearchBar
import com.serag.cryptotracker.crypto.presintation.coins_list.components.previewCoin
import com.serag.cryptotracker.ui.theme.CryptoTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinListScreen(
    state: CoinsListState,
    onAction: (CoinsLisActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    if (state.isLoading) Box(modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { focusManager.clearFocus() }
        ) {
            PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = { onAction(CoinsLisActions.RefreshCoinsList) },
            ) {

                LazyColumn(
                    modifier = modifier.fillMaxSize(),
                ) {
                    item {

                        ExpandableSearchBar(
                            query = searchQuery.text,
                            onQueryChange = {
                                searchQuery = TextFieldValue(it)
                                onAction(CoinsLisActions.OnSearch(searchQuery.text))
                            },
                            onSearchCancel = {
                                searchQuery = TextFieldValue("")
                                onAction(CoinsLisActions.OnSearch(""))
                            }
                        )

                    }
                    items(state.coins) { coinUi ->
                        CoinListItem(
                            coinUi = coinUi, onClick = {
                                focusManager.clearFocus()
                                onAction(CoinsLisActions.OnCoinClick(coinUi))
                            }, modifier = Modifier.fillMaxWidth()
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

    }
}


@PreviewLightDark
@Composable
fun CoinListScreenPreview() {
    CryptoTrackerTheme {
        CoinListScreen(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            onAction = {},
            state = CoinsListState(
                coins = MutableList(10) { previewCoin })
        )
    }
}
