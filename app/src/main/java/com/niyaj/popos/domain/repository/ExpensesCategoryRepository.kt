package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ExpensesCategoryRepository {

    suspend fun getAllExpensesCategory(): Flow<Resource<List<ExpensesCategory>>>

    suspend fun getExpensesCategoryById(expansesCategoryId: String): Resource<ExpensesCategory?>

    suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean>

    suspend fun updateExpensesCategory(newExpensesCategory: ExpensesCategory, expansesCategoryId: String): Resource<Boolean>

    suspend fun deleteExpensesCategory(expansesCategoryId: String): Resource<Boolean>
}