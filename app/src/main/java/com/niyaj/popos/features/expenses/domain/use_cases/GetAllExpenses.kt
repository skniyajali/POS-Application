package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.model.filterExpenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

/**
 *
 */
class GetAllExpenses(
    private val expensesRepository: ExpensesRepository
) {
    /**
     *
     */
    suspend operator fun invoke(
        searchText : String = "",
        startDate : String? = null,
        endDate : String? = null
    ): Flow<Resource<List<Expenses>>> {
        return channelFlow {
            expensesRepository.getAllExpenses(startDate, endDate).collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { data ->
                            data.filter { expenses ->
                                expenses.filterExpenses(searchText)
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get expanses data from repository"))
                    }
                }
            }
        }
    }
}