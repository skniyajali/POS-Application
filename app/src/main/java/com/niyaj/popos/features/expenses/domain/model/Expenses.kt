package com.niyaj.popos.features.expenses.domain.model

import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Expenses(): RealmObject {
    @PrimaryKey
    var expensesId: String = ""

    var expensesCategory: ExpensesCategory? = null

    var expensesPrice: String = ""

    var expensesRemarks: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        expensesId: String = "",
        expensesCategory: ExpensesCategory? = null,
        expensesPrice: String = "",
        expensesRemarks: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.expensesId = expensesId
        this.expensesCategory = expensesCategory
        this.expensesPrice = expensesPrice
        this.expensesRemarks = expensesRemarks
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}