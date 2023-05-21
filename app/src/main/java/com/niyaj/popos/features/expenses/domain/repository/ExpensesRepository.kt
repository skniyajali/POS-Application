package com.niyaj.popos.features.expenses.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {

    suspend fun getAllExpenses(startDate: String? = null, endDate: String? = null): Flow<Resource<List<Expenses>>>

    suspend fun getExpensesById(expensesId: String): Resource<Expenses?>

    suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean>

    suspend fun updateExpenses(newExpenses: Expenses, expensesId: String): Resource<Boolean>

    suspend fun deleteExpenses(expensesId: String): Resource<Boolean>

    suspend fun deletePastExpenses(deleteAll: Boolean): Resource<Boolean>

    suspend fun importExpenses(expenses: List<Expenses>): Resource<Boolean>
}