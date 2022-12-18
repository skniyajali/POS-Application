package com.niyaj.popos.domain.model

data class ExpensesSubCategory(
    val expansesSubCategoryId: String = "",
    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expansesSubCategoryName: String = "",
    val expansesSubCategoryDescription: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null
)
