package com.niyaj.popos.features.reports.domain.use_cases

data class ReportsUseCases(
    val generateReport: GenerateReport,
    val getReport: GetReport,
    val getReportsBarData: GetReportsBarData,
    val getProductWiseReport: GetProductWiseReport,
    val getCategoryWiseReport: GetCategoryWiseReport,
    val getAddressWiseReport: GetAddressWiseReport,
    val getCustomerWiseReport: GetCustomerWiseReport,
    val deletePastData: DeletePastData,
)
