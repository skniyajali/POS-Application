package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ExpensesCategoryRepositoryImpl(
    private val expensesCategoryRealmDao: ExpensesCategoryRealmDao
) : ExpensesCategoryRepository {

    override suspend fun getAllExpensesCategory(): Flow<Resource<List<ExpensesCategory>>> {
        return flow {
            expensesCategoryRealmDao.getAllExpansesCategory().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { expansesCategory ->
                                ExpensesCategory(
                                    expensesCategoryId = expansesCategory._id,
                                    expensesCategoryName = expansesCategory.expansesCategoryName,
                                    createdAt = expansesCategory.created_at,
                                    updatedAt = expansesCategory.updated_at,
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get expanses category from database"))
                    }
                }
            }
        }
    }

    override suspend fun getExpensesCategoryById(expansesCategoryId: String): Resource<ExpensesCategory?> {
        val result = expensesCategoryRealmDao.getExpansesCategoryById(expansesCategoryId)

        return result.data?.let { expansesCategory ->
            Resource.Success(
                data = ExpensesCategory(
                    expensesCategoryId = expansesCategory._id,
                    expensesCategoryName = expansesCategory.expansesCategoryName,
                    createdAt = expansesCategory.created_at,
                    updatedAt = expansesCategory.updated_at,
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get expenses", null)
    }

    override suspend fun createNewExpensesCategory(newExpensesCategory: ExpensesCategory): Resource<Boolean> {
        return expensesCategoryRealmDao.createNewExpansesCategory(newExpensesCategory)
    }

    override suspend fun updateExpensesCategory(
        newExpensesCategory: ExpensesCategory,
        expansesCategoryId: String,
    ): Resource<Boolean> {
        return expensesCategoryRealmDao.updateExpansesCategory(newExpensesCategory, expansesCategoryId)
    }

    override suspend fun deleteExpensesCategory(expansesCategoryId: String): Resource<Boolean> {
        return expensesCategoryRealmDao.deleteExpansesCategory(expansesCategoryId)
    }
}