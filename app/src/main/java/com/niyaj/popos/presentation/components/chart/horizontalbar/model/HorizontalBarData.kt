package com.niyaj.popos.presentation.components.chart.horizontalbar.model

data class HorizontalBarData(val xValue: Float, val yValue: Any)

internal fun List<HorizontalBarData>.maxXValue() = maxOf {
    it.xValue
}
