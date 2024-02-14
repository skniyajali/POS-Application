package com.niyaj.feature.reports

import com.niyaj.model.AddressWiseReport

data class AddressWiseReportState(
    val reports: List<AddressWiseReport> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
