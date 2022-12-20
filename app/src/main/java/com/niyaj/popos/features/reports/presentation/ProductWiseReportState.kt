package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.components.chart.horizontalbar.model.HorizontalBarData

data class ProductWiseReportState(
    val data: List<HorizontalBarData> = emptyList(),
    val orderType: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)