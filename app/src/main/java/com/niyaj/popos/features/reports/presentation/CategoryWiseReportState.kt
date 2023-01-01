package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.reports.domain.util.ProductWiseReport

data class CategoryWiseReportState(
    val categoryWiseReport: List<ProductWiseReport> = emptyList(),
    val orderType: String = "",
    val isLoading: Boolean = false,
    val hasError: String? = null
)
