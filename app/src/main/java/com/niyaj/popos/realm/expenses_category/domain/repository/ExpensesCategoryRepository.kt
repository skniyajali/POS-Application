package com.niyaj.popos.realm.expenses_category.domain.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory
import kotlinx.coroutines.flow.Flow

interface ExpensesCategoryRepository {

    suspend fun getAllExpensesCategory(): Flow<Resource<List<ExpensesCategory>>>

    suspend fun getExpensesCategoryById(expensesCategoryId: String): Resource<ExpensesCategory?>

    suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean>

    suspend fun updateExpensesCategory(newExpensesCategory: ExpensesCategory, expensesCategoryId: String): Resource<Boolean>

    suspend fun deleteExpensesCategory(expensesCategoryId: String): Resource<Boolean>
}