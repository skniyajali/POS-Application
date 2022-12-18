package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ExpensesRepository {

    suspend fun getAllExpenses(): Flow<Resource<List<Expenses>>>

    suspend fun getExpensesById(expansesId: String): Resource<Expenses?>

    suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean>

    suspend fun updateExpenses(newExpenses: Expenses, expansesId: String): Resource<Boolean>

    suspend fun deleteExpenses(expansesId: String): Resource<Boolean>

    suspend fun deletePastExpanses(deleteAll: Boolean): Resource<Boolean>
}