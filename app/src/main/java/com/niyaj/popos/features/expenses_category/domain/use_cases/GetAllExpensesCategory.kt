package com.niyaj.popos.features.expenses_category.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.model.filterExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

/**
 *
 */
class GetAllExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {
    /**
     *
     */
    suspend operator fun invoke(searchText : String = ""): Flow<Resource<List<ExpensesCategory>>> {
        return channelFlow {
            expensesCategoryRepository.getAllExpensesCategory().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { data ->
                            data.filter { expansesCategory ->
                                expansesCategory.filterExpensesCategory(searchText)
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get expanses category."))
                    }
                }
            }
        }
    }
}