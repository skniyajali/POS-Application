package com.niyaj.popos.features.components.chart.bar.config

data class BarConfig(
    val hasRoundedCorner: Boolean = false
)

internal object BarConfigDefaults {

    fun barConfigDimesDefaults() = BarConfig(
        hasRoundedCorner = false
    )
}
