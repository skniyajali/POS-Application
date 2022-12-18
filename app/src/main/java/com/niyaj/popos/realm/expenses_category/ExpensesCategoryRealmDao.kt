package com.niyaj.popos.realm.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface ExpensesCategoryRealmDao {

    suspend fun getAllExpansesCategory(): Flow<Resource<List<ExpensesCategoryRealm>>>

    suspend fun getExpansesCategoryById(expensesCategoryId: String): Resource<ExpensesCategoryRealm?>

    suspend fun createNewExpansesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean>

    suspend fun updateExpansesCategory(newExpensesCategory: ExpensesCategory, expensesCategoryId: String): Resource<Boolean>

    suspend fun deleteExpansesCategory(expensesCategoryId: String): Resource<Boolean>
}