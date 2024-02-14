package com.niyaj.ui.chart.horizontalbar.model

data class HorizontalBarData(val xValue: Float, val yValue: Any)

fun List<HorizontalBarData>.maxXValue() = maxOf {
    it.xValue
}
