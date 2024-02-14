package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.ExpensesCategory
import kotlinx.coroutines.flow.Flow

interface ExpensesCategoryRepository {

    suspend fun getAllExpensesCategory(searchText: String): Flow<List<ExpensesCategory>>

    suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?>

    suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean>

    suspend fun updateExpensesCategory(newExpensesCategory: ExpensesCategory, expensesCategoryId: String): Resource<Boolean>

    suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean>

    suspend fun deleteExpensesCategories(expensesCategoryIds: List<String>): Resource<Boolean>
}