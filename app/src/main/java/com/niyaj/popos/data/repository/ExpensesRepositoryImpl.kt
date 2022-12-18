package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.ExpensesRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExpensesRepositoryImpl(
    private val expensesRealmDao: ExpensesRealmDao,
) : ExpensesRepository {
    override suspend fun getAllExpenses(): Flow<Resource<List<Expenses>>> {
        return flow {
            expensesRealmDao.getAllExpanses().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { expanses ->
                                Expenses(
                                    expansesId = expanses._id,
                                    expensesCategory = if (expanses.expansesCategory != null) ExpensesCategory(
                                        expensesCategoryId = expanses.expansesCategory!!._id,
                                        expensesCategoryName = expanses.expansesCategory!!.expansesCategoryName,
                                        createdAt = expanses.expansesCategory!!.created_at,
                                        updatedAt = expanses.expansesCategory!!.updated_at
                                    ) else ExpensesCategory(),
                                    expansesPrice = expanses.expansesPrice,
                                    expansesRemarks = expanses.expansesRemarks,
                                    createdAt = expanses.created_at,
                                    updatedAt = expanses.updated_at,
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message
                            ?: "Unable to get employees from database"))
                    }
                }
            }
        }
    }

    override suspend fun getExpensesById(expansesId: String): Resource<Expenses?> {
        val result = expensesRealmDao.getExpansesById(expansesId)

        return result.data?.let { expanses ->
            Resource.Success(
                data = Expenses(
                    expansesId = expanses._id,
                    expensesCategory = if (expanses.expansesCategory != null) ExpensesCategory(
                        expensesCategoryId = expanses.expansesCategory!!._id,
                        expensesCategoryName = expanses.expansesCategory!!.expansesCategoryName,
                        createdAt = expanses.expansesCategory!!.created_at,
                        updatedAt = expanses.expansesCategory!!.updated_at
                    ) else ExpensesCategory(),
                    expansesPrice = expanses.expansesPrice,
                    expansesRemarks = expanses.expansesRemarks,
                    createdAt = expanses.created_at,
                    updatedAt = expanses.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get expanses item")
    }

    override suspend fun createNewExpenses(newExpenses: Expenses): Resource<Boolean> {
        return expensesRealmDao.createNewExpanses(newExpenses)
    }

    override suspend fun updateExpenses(
        newExpenses: Expenses,
        expansesId: String,
    ): Resource<Boolean> {
        return expensesRealmDao.updateExpanses(newExpenses, expansesId)
    }

    override suspend fun deleteExpenses(expansesId: String): Resource<Boolean> {
        return expensesRealmDao.deleteExpanses(expansesId)
    }

    override suspend fun deletePastExpanses(deleteAll: Boolean): Resource<Boolean> {
        return expensesRealmDao.deletePastExpanses(deleteAll)
    }
}