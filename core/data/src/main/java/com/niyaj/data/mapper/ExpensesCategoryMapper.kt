package com.niyaj.data.mapper

import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.model.ExpensesCategory

fun ExpensesCategory.toEntity(): ExpensesCategoryEntity {
    return ExpensesCategoryEntity(
        expensesCategoryId = expensesCategoryId,
        expensesCategoryName = expensesCategoryName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}