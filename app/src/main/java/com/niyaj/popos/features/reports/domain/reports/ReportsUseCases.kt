package com.niyaj.popos.features.reports.domain.reports

data class ReportsUseCases(
    val generateReport: GenerateReport,
    val getReport: GetReport,
    val getReportsBarData: GetReportsBarData,
    val getProductWiseReport: GetProductWiseReport,
    val deletePastData: DeletePastData,
)
