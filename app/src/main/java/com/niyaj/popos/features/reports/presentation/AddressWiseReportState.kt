package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.reports.domain.model.AddressWiseReport

data class AddressWiseReportState(
    val reports: List<AddressWiseReport> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
