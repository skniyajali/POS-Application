package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData

data class ReportsBarState(
    val reportBarData: List<HorizontalBarData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)