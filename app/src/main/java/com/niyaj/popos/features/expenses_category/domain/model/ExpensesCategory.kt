package com.niyaj.popos.features.expenses_category.domain.model

import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@JsonClass(generateAdapter = true)
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

fun ExpensesCategory.filterExpensesCategory(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.expensesCategoryName.contains(searchText, true) ||
                this.createdAt.contains(searchText, true) ||
                this.updatedAt?.contains(searchText, true) == true
    }else true
}