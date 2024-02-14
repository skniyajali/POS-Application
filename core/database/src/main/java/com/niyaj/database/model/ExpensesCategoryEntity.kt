package com.niyaj.database.model

import com.niyaj.model.ExpensesCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ExpensesCategoryEntity() : RealmObject {
    @PrimaryKey
    var expensesCategoryId: String = ""

    var expensesCategoryName: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        expensesCategoryId: String = "",
        expensesCategoryName: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ) : this() {
        this.expensesCategoryId = expensesCategoryId
        this.expensesCategoryName = expensesCategoryName
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}


fun ExpensesCategoryEntity.toExternalModel(): ExpensesCategory {
    return ExpensesCategory(
        expensesCategoryId = expensesCategoryId,
        expensesCategoryName = expensesCategoryName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}