package com.niyaj.popos.domain.model

import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory

data class ExpensesSubCategory(
    val expansesSubCategoryId: String = "",
    val expensesCategory: ExpensesCategory = ExpensesCategory(),
    val expansesSubCategoryName: String = "",
    val expansesSubCategoryDescription: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null
)
