package com.niyaj.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Expenses(
    val expensesId: String = "",

    val expensesCategory: ExpensesCategory? = null,

    val expensesAmount: String = "",

    val expensesDate: String = "",

    val expensesRemarks: String = "",

    val createdAt: String = "",

    val updatedAt: String? = null,
)


fun List<Expenses>.filterExpenses(searchText: String): List<Expenses> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.expensesCategory?.expensesCategoryName?.contains(searchText, true) == true ||
                    it.expensesAmount.contains(searchText, true) ||
                    it.expensesRemarks.contains(searchText, true) ||
                    it.createdAt.contains(searchText, true) ||
                    it.updatedAt?.contains(searchText, true) == true
        }
    } else this
}