package com.niyaj.popos.presentation.components.chart.horizontalbar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.niyaj.popos.presentation.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.presentation.components.chart.common.dimens.ChartDimensDefaults
import com.niyaj.popos.presentation.components.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.popos.presentation.components.chart.horizontalbar.axis.HorizontalAxisConfigDefaults
import com.niyaj.popos.presentation.components.chart.horizontalbar.axis.horizontalYAxis
import com.niyaj.popos.presentation.components.chart.horizontalbar.common.drawHorizontalBarLabel
import com.niyaj.popos.presentation.components.chart.horizontalbar.common.getBottomLeft
import com.niyaj.popos.presentation.components.chart.horizontalbar.common.getTopLeft
import com.niyaj.popos.presentation.components.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.popos.presentation.components.chart.horizontalbar.config.HorizontalBarConfigDefaults
import com.niyaj.popos.presentation.components.chart.horizontalbar.config.StartDirection
import com.niyaj.popos.presentation.components.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.popos.presentation.components.chart.horizontalbar.model.maxXValue
import com.niyaj.popos.util.toRupee

@Composable
fun HorizontalBarChart(
    horizontalBarData: List<HorizontalBarData>,
    color: Color,
    onBarClick: (HorizontalBarData) -> Unit,
    modifier: Modifier = Modifier,
    barDimens: ChartDimens = ChartDimensDefaults.horizontalChartDimesDefaults(),
    horizontalAxisConfig: HorizontalAxisConfig = HorizontalAxisConfigDefaults.axisConfigDefaults(),
    horizontalBarConfig: HorizontalBarConfig = HorizontalBarConfigDefaults.horizontalBarConfig()
) {
    HorizontalBarChart(
        horizontalBarData = horizontalBarData,
        colors = listOf(color, color),
        onBarClick = onBarClick,
        modifier = modifier,
        barDimens = barDimens,
        horizontalAxisConfig = horizontalAxisConfig,
        horizontalBarConfig = horizontalBarConfig
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun HorizontalBarChart(
    horizontalBarData: List<HorizontalBarData>,
    colors: List<Color>,
    onBarClick: (HorizontalBarData) -> Unit,
    modifier: Modifier = Modifier,
    barDimens: ChartDimens = ChartDimensDefaults.horizontalChartDimesDefaults(),
    horizontalAxisConfig: HorizontalAxisConfig = HorizontalAxisConfigDefaults.axisConfigDefaults(),
    horizontalBarConfig: HorizontalBarConfig = HorizontalBarConfigDefaults.horizontalBarConfig()
) {
    val labelTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    val startAngle = if (horizontalBarConfig.startDirection == StartDirection.Left) 180F else 0F
//    val maxXValueState = rememberSaveable { mutableStateOf(horizontalBarData.maxXValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }
    val maxXValue = horizontalBarData.maxXValue()
    val barHeight = remember { mutableStateOf(0F) }
    val chartBound = remember { mutableStateOf(0F) }
    val textMeasurer = rememberTextMeasurer()
    val productReport = horizontalBarConfig.productReport

    Canvas(
        modifier = modifier
            .drawBehind {
                if (horizontalAxisConfig.showAxes) {
                    horizontalYAxis(horizontalAxisConfig, maxXValue, startAngle)
                }
            }
            .padding(horizontal = barDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            }
    ) {
        barHeight.value = size.height.div(horizontalBarData.count().times(1.2F))
        chartBound.value = size.width.div(horizontalBarData.count().times(1.2F))

        val xScalableFactor = size.width.div(maxXValue)

        when (horizontalBarConfig.startDirection) {
            StartDirection.Right -> {
                horizontalBarData.forEachIndexed { index, data ->
                    val topLeft = getTopLeft(index, barHeight, size, data, xScalableFactor)
                    val bottomLeft = getBottomLeft(index, barHeight, size, data, xScalableFactor)
                    val barWidth = data.xValue.times(xScalableFactor)

                    if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                        onBarClick(data)
                    }
                    drawBars(
                        horizontalBarData = data,
                        barHeight = barHeight.value,
                        colors = colors,
                        showLabels = horizontalBarConfig.showLabels,
                        topLeft = topLeft,
                        barWidth = barWidth,
                        labelTextColor = labelTextColor,
                        textMeasurer = textMeasurer,
                        isProductReport = productReport
                    )
                }
            }
            else -> {
                horizontalBarData.forEachIndexed { index, data ->
                    val barWidth = data.xValue.times(xScalableFactor)
                    val topLeft = Offset(0F, barHeight.value.times(index).times(1.2F))
                    val bottomLeft = getBottomLeft(index, barHeight, size, data, xScalableFactor)

                    if (clickedBar.value.y in (topLeft.y..bottomLeft.y)) {
                        onBarClick(data)
                    }

                    drawBars(
                        horizontalBarData = data,
                        barHeight = barHeight.value,
                        colors = colors,
                        showLabels = horizontalBarConfig.showLabels,
                        topLeft = topLeft,
                        barWidth = barWidth,
                        labelTextColor = labelTextColor,
                        textMeasurer = textMeasurer,
                        isProductReport = productReport
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawBars(
    horizontalBarData: HorizontalBarData,
    barHeight: Float,
    colors: List<Color>,
    showLabels: Boolean,
    topLeft: Offset,
    barWidth: Float,
    labelTextColor: Color,
    textMeasurer: TextMeasurer,
    isProductReport: Boolean,
) {

    val drawableText = if (isProductReport) {
        "${horizontalBarData.yValue} - ${horizontalBarData.xValue.toString().substringBefore(".")} Qty"
    } else {
        "${horizontalBarData.yValue} - ${horizontalBarData.xValue.toString().substringBefore(".").toRupee}"
    }

    val textColor = if(horizontalBarData.xValue.toString().substringBefore(".").toLong() <= 0) {
        Color.Black
    } else {
        Color.White
    }

    drawRoundRect(
        cornerRadius = CornerRadius(x = 4F, y =  4F),
        topLeft = topLeft,
        brush = Brush.linearGradient(colors),
        size = Size(barWidth, barHeight)
    )

    drawText(
        textMeasurer = textMeasurer,
        text = drawableText,
        topLeft = Offset(topLeft.x.plus(20), topLeft.y.plus(20)),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(
            color = textColor,
            fontWeight = FontWeight.Normal,
        ),
//        maxSize = IntSize(barWidth.toInt(), barHeight.toInt())
    )

    if (showLabels) {
        drawHorizontalBarLabel(
            horizontalBarData = horizontalBarData,
            barHeight = barHeight,
            topLeft = topLeft,
            labelTextColor = labelTextColor,
        )
    }
}
