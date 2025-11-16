package com.serag.cryptotracker.crypto.presintation.coin_detail.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.serag.cryptotracker.crypto.domain.CoinPrice
import com.serag.cryptotracker.crypto.presintation.coin_detail.ChartStyle
import com.serag.cryptotracker.crypto.presintation.coin_detail.DataPoint
import com.serag.cryptotracker.crypto.presintation.coin_detail.ValueLapel
import com.serag.cryptotracker.crypto.presintation.coin_detail.toDataPoint
import com.serag.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import kotlin.random.Random


@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    style: ChartStyle,
    dataPoints: List<DataPoint>,
    visibleDataPointsIndices: IntRange,
    unit: String,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint?) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},

    showHelperLines: Boolean = true,
) {

    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize, fontFamily = FontFamily.Default
    )

    val visibleDataPoints = remember(
        dataPoints, visibleDataPointsIndices
    ) { dataPoints.slice(visibleDataPointsIndices) }

    val minY = remember(visibleDataPoints) { visibleDataPoints.minOfOrNull { it.y } ?: 0f }
    val maxY = remember(visibleDataPoints) { visibleDataPoints.maxOfOrNull { it.y } ?: 0f }

    val measurer = rememberTextMeasurer()

    var xLabelWidth by remember { mutableFloatStateOf(0f) }
    var yLabelWidth by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(xLabelWidth, yLabelWidth) {
        println("linechart X LABEL WIDTH: $xLabelWidth")
        println("linechart VISIBLE DATA POINTS SIZE: ${visibleDataPoints.size}")
        onXLabelWidthChange(xLabelWidth * visibleDataPoints.size + yLabelWidth + 10f)
    }

    val selectedDataPointIndex =
        remember(selectedDataPoint) { visibleDataPoints.indexOf(selectedDataPoint) }
    var drawPoints by remember { mutableStateOf(listOf<DataPoint>()) }
    var isShowingDataPoints by remember { mutableStateOf(selectedDataPoint != null) }

    Canvas(
        modifier = modifier
            .pointerInput(
                drawPoints, xLabelWidth
            ) {
                detectTapGestures { tapOffset ->
                    val tappedIndex = drawPoints.indexOfFirst {
                        (tapOffset.x >= it.x - xLabelWidth / 2f) &&
                                (tapOffset.x <= it.x + xLabelWidth / 2f)
                    }
                    if (tappedIndex != -1) {

                        onSelectedDataPoint(dataPoints[visibleDataPointsIndices.first + tappedIndex])
                        isShowingDataPoints = true
                    } else {
                        onSelectedDataPoint(null)

                        isShowingDataPoints = false
                    }

                }
            },
    ) {
        val minLabelSpacingYPx = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()

        val xLabelTextLayoutResults = visibleDataPoints.map {
            measurer.measure(it.xLapel, textStyle.copy(textAlign = TextAlign.Center))
        }
        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width.toFloat() } ?: 0f
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height.toFloat() } ?: 0f
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 1
        val xLabelLineHeight = maxXLabelHeight / maxXLabelLineCount


        val viewPortHeightPx =
            size.height - (verticalPaddingPx * 2 + maxXLabelHeight + xAxisLabelSpacingPx + xLabelLineHeight)
        // Y Labels Calculations
        val lapelViewPortHeight = viewPortHeightPx + xLabelLineHeight
        val lapelCountsExcludingLast =
            (lapelViewPortHeight / (xLabelLineHeight + minLabelSpacingYPx)).toInt()

        val valueIncrement = (maxY - minY) / lapelCountsExcludingLast

        val yLabels = (0..lapelCountsExcludingLast).map {
            ValueLapel(
                value = maxY - it * valueIncrement,
                unit
            )
        }
        val yLabelsTextLayoutResults = yLabels.map {
            measurer.measure(it.formatted(), textStyle)
        }
        val maxYLabelWidth = yLabelsTextLayoutResults.maxOfOrNull { it.size.width.toFloat() } ?: 0f

        val viewPortTopY = verticalPaddingPx + xLabelLineHeight + 10f
        val viewPortRightX = size.width

        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = horizontalPaddingPx * 2f + maxYLabelWidth


        xLabelWidth = maxXLabelWidth + xAxisLabelSpacingPx
        xLabelTextLayoutResults.forEachIndexed { index, textLayoutResult ->
            val color =
                if (index == selectedDataPointIndex) style.selectedColor else style.unselectedColor
            val x = viewPortLeftX + xAxisLabelSpacingPx / 2f + xLabelWidth * index
            drawText(
                textLayoutResult,
                topLeft = Offset(
                    x = x,
                    y = viewPortBottomY + xAxisLabelSpacingPx
                ),
                color = color
            )
            if (selectedDataPointIndex == index) {
                val selectedLapel = ValueLapel(dataPoints[selectedDataPointIndex].y, unit)
                val selectedLapelTextLayoutResult =
                    measurer.measure(
                        selectedLapel.formatted(),
                        textStyle.copy(color = style.selectedColor), maxLines = 1
                    )
                val isLastIndex = selectedDataPointIndex == visibleDataPoints.lastIndex
                val selectedLapelTextXPosition = if (isLastIndex) {
                    x - selectedLapelTextLayoutResult.size.width
                } else {
                    x - selectedLapelTextLayoutResult.size.width / 2f
                } +
                        textLayoutResult.size.width / 2f

                val isInViewPortHorizontally =
                    selectedLapelTextXPosition <= (selectedLapelTextXPosition + selectedLapelTextLayoutResult.size.width)
                if (isInViewPortHorizontally)
                    drawText(
                        selectedLapelTextLayoutResult,
                        topLeft = Offset(
                            x = selectedLapelTextXPosition,
                            y = viewPortTopY - selectedLapelTextLayoutResult.size.height - 10f
                        ),
                        color = style.selectedColor,

                        )

            }
            if (showHelperLines)
                drawLine(
                    color = color,
                    start = Offset(
                        x = x + maxXLabelWidth / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + maxXLabelWidth / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if (index == selectedDataPointIndex) style.axisLinesThicknessPx else style.helperLinesThicknessPx
                )
        }


        val lapelsHighRequired = xLabelLineHeight * (lapelCountsExcludingLast + 1)
        val allLapelSpaces = lapelViewPortHeight - lapelsHighRequired

        val lapelSpacing = allLapelSpaces / lapelCountsExcludingLast

        yLabelWidth = maxYLabelWidth + horizontalPaddingPx
        yLabelsTextLayoutResults.forEachIndexed { index, textLayoutResult ->
            val x = horizontalPaddingPx + maxYLabelWidth - textLayoutResult.size.width
            val y =
                (viewPortTopY - xLabelLineHeight / 2) + index * (lapelSpacing + xLabelLineHeight)
            drawText(
                textLayoutResult,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = style.unselectedColor
            )

            if (showHelperLines)
                drawLine(
                    color = style.unselectedColor,
                    start = Offset(
                        x = viewPortLeftX,
                        y = y + textLayoutResult.size.height / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + textLayoutResult.size.height / 2f
                    ),
                    strokeWidth = style.helperLinesThicknessPx
                )
        }

        val conPoints0 = mutableListOf<DataPoint>()
        val conPoints1 = mutableListOf<DataPoint>()

        for (i in 1 until drawPoints.size) {
            val p0 = drawPoints[i - 1]
            val p1 = drawPoints[i]
            val x = (p0.x + p1.x) / 2f
            val y0 = p0.y
            val y1 = p1.y
            conPoints0.add(DataPoint(x = x, y = y0, xLapel = ""))
            conPoints1.add(DataPoint(x = x, y = y1, xLapel = ""))
        }
        val linePath = Path().apply {
            if (drawPoints.isNotEmpty()) {
                moveTo(drawPoints[0].x, drawPoints[0].y)
                for (i in 1 until drawPoints.size) {
                    cubicTo(
                        conPoints0[i - 1].x,
                        conPoints0[i - 1].y,
                        conPoints1[i - 1].x,
                        conPoints1[i - 1].y,
                        drawPoints[i].x,
                        drawPoints[i].y
                    )
                }
            }
        }
        drawPath(
            path = linePath,
            color = style.chartLineColor,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 5f,
                cap = StrokeCap.Round
            )
        )

        drawPoints = visibleDataPointsIndices.map {
            val x =
                viewPortLeftX + (it - visibleDataPointsIndices.first) * xLabelWidth + xLabelWidth / 2f
            val normalizedY = (dataPoints[it].y - minY) / (maxY - minY)
            val y = viewPortBottomY - normalizedY * viewPortHeightPx

            DataPoint(
                x = x,
                y = y,
                xLapel = dataPoints[it].xLapel
            )
        }
        if (isShowingDataPoints) {

            drawPoints.forEachIndexed { index, point ->
                drawCircle(
                    color = style.selectedColor,
                    radius = if (index == selectedDataPointIndex) 15f else 10f,
                    center = Offset(
                        x = point.x,
                        y = point.y
                    )
                )
                if (selectedDataPointIndex == index) {
                    drawCircle(
                        color = Color.White,
                        radius = 10f,
                        center = Offset(
                            x = point.x,
                            y = point.y
                        )
                    )
                }


            }
        }
    }

}


@Preview(widthDp = 1000)
@Composable
private fun LineChartPreview() {
    var selectedDataPointIndex by remember { mutableStateOf<DataPoint?>(null) }
    val state = rememberScrollState()
    CryptoTrackerTheme {
        val coinHistoryRandomized = remember {
            (1..20).map {
                CoinPrice(
                    priceUsd = Random.nextFloat() * 1000.0,
                    dateTime = ZonedDateTime.now().plusHours(it.toLong())
                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.Black,
            unselectedColor = Color(0xFF7C7C7C),
            selectedColor = Color.Black,
            helperLinesThicknessPx = 1f,
            axisLinesThicknessPx = 3f,
            labelFontSize = 14.sp,
            minYLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp
        )
        val dataPoints = remember {
            coinHistoryRandomized.map { it.toDataPoint() }
        }
        Box(
            modifier = Modifier
                .horizontalScroll(state)
        ) {
            LineChart(
                dataPoints = dataPoints,
                style = style,
                visibleDataPointsIndices = 0..10,
                unit = "$",
                onSelectedDataPoint = {
                    selectedDataPointIndex = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .width(700.dp)
                    .height(300.dp)
                    .background(Color.White),
                selectedDataPoint = selectedDataPointIndex
            )
        }
    }
}