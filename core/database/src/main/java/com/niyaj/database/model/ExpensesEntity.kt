package com.niyaj.database.model

import com.niyaj.model.Expenses
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ExpensesEntity(): RealmObject {
    @PrimaryKey
    var expensesId: String = ""

    var expensesCategory: ExpensesCategoryEntity? = null

    var expensesAmount: String = ""

    var expensesDate: String = ""

    var expensesRemarks: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        expensesId: String = "",
        expensesCategory: ExpensesCategoryEntity? = null,
        expensesPrice: String = "",
        expensesDate: String = "",
        expensesRemarks: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.expensesId = expensesId
        this.expensesCategory = expensesCategory
        this.expensesAmount = expensesPrice
        this.expensesDate = expensesDate
        this.expensesRemarks = expensesRemarks
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}


fun ExpensesEntity.toExternalModel(): Expenses {
    return Expenses(
        expensesId = expensesId,
        expensesCategory = expensesCategory?.toExternalModel(),
        expensesAmount = expensesAmount,
        expensesDate = expensesDate,
        expensesRemarks = expensesRemarks,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}