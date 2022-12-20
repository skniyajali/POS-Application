package com.niyaj.popos.features.components.chart.bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.niyaj.popos.features.components.chart.bar.common.calculations.getTopLeft
import com.niyaj.popos.features.components.chart.bar.common.calculations.getTopRight
import com.niyaj.popos.features.components.chart.bar.common.component.drawBarLabel
import com.niyaj.popos.features.components.chart.bar.config.BarConfig
import com.niyaj.popos.features.components.chart.bar.config.BarConfigDefaults
import com.niyaj.popos.features.components.chart.bar.model.BarData
import com.niyaj.popos.features.components.chart.bar.model.GroupedBarData
import com.niyaj.popos.features.components.chart.bar.model.maxYValue
import com.niyaj.popos.features.components.chart.bar.model.totalItems
import com.niyaj.popos.features.components.chart.common.axis.AxisConfig
import com.niyaj.popos.features.components.chart.common.axis.AxisConfigDefaults
import com.niyaj.popos.features.components.chart.common.axis.drawYAxisWithLabels
import com.niyaj.popos.features.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.features.components.chart.common.dimens.ChartDimensDefaults

@Composable
fun GroupedBarChart(
    groupedBarData: List<GroupedBarData>,
    modifier: Modifier = Modifier,
    onBarClick: (BarData) -> Unit = {},
    barDimens: ChartDimens = ChartDimensDefaults.chartDimesDefaults(),
    axisConfig: AxisConfig = AxisConfigDefaults.axisConfigDefaults(isSystemInDarkTheme()),
    barConfig: BarConfig = BarConfigDefaults.barConfigDimesDefaults()
) {
    val barWidth = remember { mutableStateOf(0F) }
    val maxYValueState = rememberSaveable { mutableStateOf(groupedBarData.maxYValue()) }
    val clickedBar = remember { mutableStateOf(Offset(-10F, -10F)) }
    val maxYValue = maxYValueState.value

    val totalItems: Int = groupedBarData.totalItems()
    Canvas(
        modifier = modifier
            .drawBehind {
                if (axisConfig.showAxis) {
                    drawYAxisWithLabels(axisConfig, maxYValue, textColor = axisConfig.textColor)
                }
            }
            .padding(horizontal = barDimens.padding)
            .pointerInput(Unit) {
                detectTapGestures(onPress = { offset ->
                    clickedBar.value = offset
                })
            }
    ) {
        barWidth.value = size.width.div(totalItems.times(1.2F))
        val yScalableFactor = size.height.div(maxYValue)
        val groupedBarDataColor: List<Color> = groupedBarData.flatMap { it.colors }
        val groupedBarDataCount = groupedBarData.flatMap { it.barData }.count()

        if (groupedBarDataColor.count() != groupedBarDataCount) throw Exception("Total colors cannot be more then $groupedBarDataCount")

        groupedBarData.flatMap { it.barData }
            .forEachIndexed { index, data ->
                val topLeft = getTopLeft(index, barWidth.value, size, data.yValue, yScalableFactor)
                val topRight =
                    getTopRight(index, barWidth.value, size, data.yValue, yScalableFactor)
                val barHeight = data.yValue.times(yScalableFactor)

                if (clickedBar.value.x in (topLeft.x..topRight.x)) {
                    onBarClick(data)
                }
                drawRoundRect(
                    cornerRadius = CornerRadius(if (barConfig.hasRoundedCorner) barHeight else 0F),
                    topLeft = topLeft,
                    color = groupedBarDataColor[index],
                    size = Size(barWidth.value, barHeight)
                )

                if (axisConfig.showXLabels) {
                    drawBarLabel(
                        data.xValue,
                        barWidth.value,
                        barHeight,
                        topLeft,
                        groupedBarData.count(),
                        axisConfig.textColor
                    )
                }
            }
    }
}
