package com.niyaj.feature.reports

import com.niyaj.ui.chart.horizontalbar.model.HorizontalBarData
import com.niyaj.model.OrderType

data class ProductWiseReportState(
    val data: List<HorizontalBarData> = emptyList(),
    val orderType: OrderType? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)