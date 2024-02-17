package com.niyaj.data.mapper

import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.model.ExpensesCategory
import org.mongodb.kbson.BsonObjectId

fun ExpensesCategory.toEntity(): ExpensesCategoryEntity {
    return ExpensesCategoryEntity(
        expensesCategoryId = expensesCategoryId.ifEmpty { BsonObjectId().toHexString() },
        expensesCategoryName = expensesCategoryName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}