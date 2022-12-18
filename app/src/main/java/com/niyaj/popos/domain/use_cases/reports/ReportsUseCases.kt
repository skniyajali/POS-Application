package com.niyaj.popos.domain.use_cases.reports

data class ReportsUseCases(
    val generateReport: GenerateReport,
    val getReport: GetReport,
    val getReportsBarData: GetReportsBarData,
    val getProductWiseReport: GetProductWiseReport,
    val deletePastData: DeletePastData,
)
