package com.niyaj.feature.reports

import com.niyaj.model.OrderType
import com.niyaj.model.ProductWiseReport

data class CategoryWiseReportState(
    val categoryWiseReport: List<ProductWiseReport> = emptyList(),
    val orderType: OrderType? = null,
    val isLoading: Boolean = true,
    val hasError: String? = null
)
