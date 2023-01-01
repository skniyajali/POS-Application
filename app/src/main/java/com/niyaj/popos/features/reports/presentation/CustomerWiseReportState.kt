package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.reports.domain.model.CustomerWiseReport

data class CustomerWiseReportState(
    val reports: List<CustomerWiseReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
