package com.niyaj.popos.features.expenses.domain.model

import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@JsonClass(generateAdapter = true)
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

fun Expenses.filterExpenses(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.expensesCategory?.expensesCategoryName?.contains(searchText, true) == true ||
                this.expensesPrice.contains(searchText, true) ||
                this.expensesRemarks.contains(searchText, true) ||
                this.createdAt.contains(searchText, true) ||
                this.updatedAt?.contains(searchText, true) == true
    } else true
}