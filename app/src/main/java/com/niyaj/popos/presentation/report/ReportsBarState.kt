package com.niyaj.popos.presentation.report

import com.niyaj.popos.presentation.components.chart.horizontalbar.model.HorizontalBarData

data class ReportsBarState(
    val reportBarData: List<HorizontalBarData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)