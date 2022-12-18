package com.niyaj.popos.presentation.report

import com.niyaj.popos.presentation.components.chart.horizontalbar.model.HorizontalBarData

data class ProductWiseReportState(
    val data: List<HorizontalBarData> = emptyList(),
    val orderType: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)