package com.niyaj.popos.domain.model

data class Expenses(
    val expansesId: String = "",
    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expansesPrice: String = "",
    val expansesRemarks: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null
)
