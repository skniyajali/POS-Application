package com.niyaj.popos.domain.model

data class Reports(
    val reportId: String = "",

    val expensesQty: Long = 0,
    val expensesAmount: Long = 0,

    val dineInSalesQty: Long = 0,
    val dineInSalesAmount: Long = 0,

    val dineOutSalesQty: Long = 0,
    val dineOutSalesAmount: Long = 0,

    val reportDate: String = "",

    val createdAt: String = "",

    val updatedAt: String? = null
)

data class ReportBoxData(
    val id: String,
    val title: String,
    val description: String = "",
    val amount: String
)

data class ReportBarData(
    val data: List<Pair<String, Number>> = emptyList(),
)

data class ProductWiseReport(
    val product: Product? = null,
    val quantity: Int
)

data class ProductWiseReportRealm(
    val productId: String,
    val quantity: Int
)