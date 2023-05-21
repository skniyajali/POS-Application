package com.niyaj.popos.features.reports.domain.use_cases

data class ReportsUseCases(
    val getReportsBarData: GetReportsBarData,
    val getProductWiseReport: GetProductWiseReport,
    val getAddressWiseReport: GetAddressWiseReport,
    val getCustomerWiseReport: GetCustomerWiseReport,
)
