package com.niyaj.popos.features.expenses_category.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ExpensesCategory(): RealmObject{
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
    ): this() {
        this.expensesCategoryId = expensesCategoryId
        this.expensesCategoryName = expensesCategoryName
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}