package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.ExpensesCategory
import kotlinx.coroutines.flow.Flow

interface ExpensesCategoryRepository {

    suspend fun getAllExpensesCategory(searchText: String): Flow<List<ExpensesCategory>>

    suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?>

    suspend fun createOrUpdateCategory(newCategory: ExpensesCategory, categoryId: String): Resource<Boolean>

    suspend fun deleteExpensesCategories(expensesCategoryIds: List<String>): Resource<Boolean>
}