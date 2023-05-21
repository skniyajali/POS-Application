package com.niyaj.popos.features.reports.presentation

import com.niyaj.popos.features.reports.domain.model.Reports

data class ReportState(
    val report: Reports = Reports(),
    val isLoading: Boolean = false,
    val hasError: String? = null,
)