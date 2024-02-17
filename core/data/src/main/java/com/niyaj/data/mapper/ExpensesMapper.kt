package com.niyaj.data.mapper

import com.niyaj.database.model.ExpensesEntity
import com.niyaj.model.Expenses
import org.mongodb.kbson.BsonObjectId


fun Expenses.toEntity(): ExpensesEntity {
    return ExpensesEntity(
        expensesId = expensesId.ifEmpty { BsonObjectId().toHexString() },
        expensesCategory = expensesCategory?.toEntity(),
        expensesPrice = expensesAmount,
        expensesRemarks = expensesRemarks,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}