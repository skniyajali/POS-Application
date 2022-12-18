package com.niyaj.popos.realm.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ExpensesRealmDao {

    suspend fun getAllExpanses(): Flow<Resource<List<ExpensesRealm>>>

    suspend fun getExpansesById(expansesId: String): Resource<ExpensesRealm?>

    suspend fun createNewExpanses(newExpenses: Expenses): Resource<Boolean>

    suspend fun updateExpanses(newExpenses: Expenses, expansesId: String): Resource<Boolean>

    suspend fun deleteExpanses(expansesId: String): Resource<Boolean>

    suspend fun deletePastExpanses(deleteAll: Boolean): Resource<Boolean>
}