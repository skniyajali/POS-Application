package com.niyaj.feature.reports

import com.niyaj.ui.chart.horizontalbar.model.HorizontalBarData

data class ReportsBarState(
    val reportBarData: List<HorizontalBarData> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)