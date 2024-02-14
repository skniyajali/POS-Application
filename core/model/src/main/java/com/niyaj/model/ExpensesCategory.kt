package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExpensesCategory(
    val expensesCategoryId: String = "",

    val expensesCategoryName: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


fun List<ExpensesCategory>.filterExpensesCategory(searchText: String): List<ExpensesCategory> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.expensesCategoryName.contains(searchText, true) ||
                    it.createdAt.contains(searchText, true) ||
                    it.updatedAt?.contains(searchText, true) == true
        }
    } else this
}