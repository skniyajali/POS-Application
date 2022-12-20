package com.niyaj.popos.features.components.chart.horizontalbar.config

data class HorizontalBarConfig(
    val showLabels: Boolean = true,
    val startDirection: StartDirection,
    val productReport: Boolean = true,
)

internal object HorizontalBarConfigDefaults {

    fun horizontalBarConfig() = HorizontalBarConfig(
        showLabels = true,
        startDirection = StartDirection.Right,
        productReport = true,
    )
}

sealed interface StartDirection {
    object Left : StartDirection
    object Right : StartDirection
}
