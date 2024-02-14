package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Expenses
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {

    suspend fun getAllExpenses(searchText: String, expensesDate: String?): Flow<List<Expenses>>

    suspend fun getExpensesById(expensesId: String): Resource<Expenses?>

    suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean>

    suspend fun updateExpenses(newExpenses: Expenses, expensesId: String): Resource<Boolean>

    suspend fun deleteExpense(expensesId: String): Resource<Boolean>

    suspend fun deleteExpenses(expensesIds: List<String>): Resource<Boolean>

    suspend fun deletePastExpenses(deleteAll: Boolean): Resource<Boolean>

    suspend fun importExpenses(expenses: List<Expenses>): Resource<Boolean>
}