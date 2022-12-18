package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpenses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetAllExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(
        filterExpanses: FilterExpenses = FilterExpenses.ByExpensesCategory(SortType.Descending),
        searchText: String = ""
    ): Flow<Resource<List<Expenses>>> {
        return flow {
            expensesRepository.getAllExpenses().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { data ->
                                when (filterExpanses.sortType) {
                                    is SortType.Ascending -> {
                                        when (filterExpanses) {
                                            is FilterExpenses.ByExpensesId -> {
                                                data.sortedBy { it.expansesId }
                                            }
                                            is FilterExpenses.ByExpensesPrice -> {
                                                data.sortedBy { it.expansesPrice }
                                            }
                                            is FilterExpenses.ByExpensesCategory -> {
                                                data.sortedBy { it.expensesCategory.expensesCategoryId }
                                            }
                                            is FilterExpenses.ByExpensesRemarks -> {
                                                data.sortedBy { it.expansesRemarks }
                                            }
                                            is FilterExpenses.ByExpensesDate -> {
                                                data.sortedBy { it.createdAt }
                                            }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when (filterExpanses) {
                                            is FilterExpenses.ByExpensesId -> {
                                                data.sortedByDescending { it.expansesId }
                                            }
                                            is FilterExpenses.ByExpensesPrice -> {
                                                data.sortedByDescending { it.expansesPrice }
                                            }
                                            is FilterExpenses.ByExpensesCategory -> {
                                                data.sortedByDescending { it.expensesCategory.expensesCategoryId }
                                            }
                                            is FilterExpenses.ByExpensesRemarks -> {
                                                data.sortedByDescending { it.expansesRemarks }
                                            }
                                            is FilterExpenses.ByExpensesDate -> {
                                                data.sortedByDescending { it.createdAt }
                                            }
                                        }
                                    }
                                }.filter { expenses ->
                                    expenses.expensesCategory.expensesCategoryName.contains(searchText, true) ||
                                    expenses.expansesPrice.contains(searchText, true) ||
                                    expenses.expansesRemarks.contains(searchText, true) ||
                                    expenses.createdAt?.contains(searchText, true) == true ||
                                    expenses.updatedAt?.contains(searchText, true) == true
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        Timber.d("Unable to get expanses data from repository")
                        emit(Resource.Error(result.message ?: "Unable to get expanses data from repository"))
                    }
                }
            }
        }
    }
}