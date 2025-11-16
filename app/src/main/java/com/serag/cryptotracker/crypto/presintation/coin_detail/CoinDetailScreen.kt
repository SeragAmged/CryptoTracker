package com.serag.cryptotracker.crypto.presintation.coin_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serag.cryptotracker.R
import com.serag.cryptotracker.crypto.domain.CoinPrice
import com.serag.cryptotracker.crypto.presintation.coin_detail.components.InfoCard
import com.serag.cryptotracker.crypto.presintation.coin_detail.components.LineChart
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsLisActions
import com.serag.cryptotracker.crypto.presintation.coins_list.CoinsListState
import com.serag.cryptotracker.crypto.presintation.coins_list.components.previewCoin
import com.serag.cryptotracker.crypto.presintation.models.toDisplayableNumber
import com.serag.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailsScreen(
    state: CoinsListState,
    onRefresh: (CoinsLisActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }
    val scrollState = rememberScrollState()

    if (state.isLoading) Box(modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
    else if (state.selectedCoin != null) {
        val coin = state.selectedCoin
        PullToRefreshBox(
            state.isRefreshing,
            { onRefresh(CoinsLisActions.RefreshSelectedCoinPrices) }
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    imageVector = ImageVector.vectorResource(coin.symbolResId),
                    contentDescription = coin.name,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = coin.name,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )
                Text(
                    text = coin.symbol,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = contentColor
                )

                FlowRow(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    InfoCard(
                        icon = ImageVector.vectorResource(R.drawable.stock),
                        title = stringResource(R.string.market_cap),
                        formattedText = coin.marketCapUsd.formatted,
                    )
                    InfoCard(
                        icon = ImageVector.vectorResource(R.drawable.dollar),
                        title = stringResource(R.string.price),
                        formattedText = coin.priceUsd.formatted,
                        minWidth = 180.dp
                    )
                    val absoluteChangeFormatted =
                        (coin.priceUsd.value * (coin.changePercent24Hr.value / 100)).toDisplayableNumber()
                    val isPositive = coin.changePercent24Hr.value > 0.0
                    val contentColor = if (isPositive) {
                        Color.Green.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                    InfoCard(
                        title = stringResource(R.string.change_last_24h),
                        formattedText = absoluteChangeFormatted.formatted,
                        icon = if (isPositive) ImageVector.vectorResource(R.drawable.trending) else ImageVector.vectorResource(
                            R.drawable.trending_down
                        ),
                        contentColor = contentColor
                    )
                }

                val style = ChartStyle(
                    chartLineColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.secondary.copy(
                        alpha = 0.3f
                    ), selectedColor = MaterialTheme.colorScheme.primary,
                    helperLinesThicknessPx = 5f,
                    axisLinesThicknessPx = 5f,
                    labelFontSize = 14.sp,
                    minYLabelSpacing = 25.dp,
                    verticalPadding = 8.dp,
                    horizontalPadding = 8.dp,
                    xAxisLabelSpacing = 8.dp
                )
                var selectedDataPointIndex by remember { mutableStateOf<DataPoint?>(null) }
                var chartWidthPx by remember { mutableFloatStateOf(0f) }
                val density = LocalDensity.current
                // convert px -> dp for modifier when available
                val chartWidthDp = with(density) { chartWidthPx.toDp() }
                AnimatedVisibility(
                    visible = state.selectedCoin.priceHistory.isNotEmpty()
                ) {
                    Box(
                        Modifier.horizontalScroll(scrollState)
                    ) {
                        LineChart(
                            dataPoints = state.selectedCoin.priceHistory,
                            visibleDataPointsIndices = state.selectedCoin.priceHistory.indices,
                            style = style,
                            unit = "$",
                            onSelectedDataPoint = { selectedDataPointIndex = it },
                            onXLabelWidthChange = { px ->
                                chartWidthPx = px
                            }, modifier = Modifier
                                .width(chartWidthDp)
                                .height(
                                    (LocalConfiguration.current
                                        .screenWidthDp * (9 / 16f)).dp
                                ),
                            selectedDataPoint = selectedDataPointIndex
                        )
                    }
                }

            }
        }
    }
}


@PreviewLightDark
//@PreviewDynamicColors
@Composable
private fun CoinDetailsScreenPreview() {
    CryptoTrackerTheme {
        Scaffold { padding ->

            CoinDetailsScreen(
                state = CoinsListState(
                    selectedCoin = previewCoin.copy(
                        priceHistory = (1..20).map {
                            CoinPrice(
                                priceUsd = Random.nextFloat() * 1000.0,
                                dateTime = ZonedDateTime.now().plusHours(it.toLong())
                            ).toDataPoint()
                        }
                    )),
                onRefresh = {}, modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background
                    )
                    .padding(padding)
            )
        }
    }
}