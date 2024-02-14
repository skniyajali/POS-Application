package com.niyaj.data.mapper

import com.niyaj.database.model.ExpensesEntity
import com.niyaj.model.Expenses


fun Expenses.toEntity(): ExpensesEntity {
    return ExpensesEntity(
        expensesId = expensesId,
        expensesCategory = expensesCategory?.toEntity(),
        expensesPrice = expensesAmount,
        expensesRemarks = expensesRemarks,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}