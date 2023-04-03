package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.util.FilterExpenses
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllExpenses(
    private val expensesRepository: ExpensesRepository
) {
    suspend operator fun invoke(
        filterExpanses: FilterExpenses = FilterExpenses.ByExpensesCategory(SortType.Descending),
        searchText: String = "",
        startDate: String,
        endDate: String
    ): Flow<Resource<List<Expenses>>> {
        return channelFlow {
            expensesRepository.getAllExpenses(startDate, endDate).collectLatest{ result ->
                when(result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let { data ->
                            when (filterExpanses.sortType) {
                                is SortType.Ascending -> {
                                    when (filterExpanses) {
                                        is FilterExpenses.ByExpensesId -> {
                                            data.sortedBy { it.expensesId }
                                        }
                                        is FilterExpenses.ByExpensesPrice -> {
                                            data.sortedBy { it.expensesPrice }
                                        }
                                        is FilterExpenses.ByExpensesCategory -> {
                                            data.sortedBy { it.expensesCategory?.expensesCategoryId }
                                        }
                                        is FilterExpenses.ByExpensesRemarks -> {
                                            data.sortedBy { it.expensesRemarks }
                                        }
                                        is FilterExpenses.ByExpensesDate -> {
                                            data.sortedBy { it.createdAt }
                                        }
                                    }
                                }
                                is SortType.Descending -> {
                                    when (filterExpanses) {
                                        is FilterExpenses.ByExpensesId -> {
                                            data.sortedByDescending { it.expensesId }
                                        }
                                        is FilterExpenses.ByExpensesPrice -> {
                                            data.sortedByDescending { it.expensesPrice }
                                        }
                                        is FilterExpenses.ByExpensesCategory -> {
                                            data.sortedByDescending { it.expensesCategory?.expensesCategoryId }
                                        }
                                        is FilterExpenses.ByExpensesRemarks -> {
                                            data.sortedByDescending { it.expensesRemarks }
                                        }
                                        is FilterExpenses.ByExpensesDate -> {
                                            data.sortedByDescending { it.createdAt }
                                        }
                                    }
                                }
                            }.filter { expenses ->
                                expenses.expensesCategory?.expensesCategoryName?.contains(searchText, true) == true ||
                                        expenses.expensesPrice.contains(searchText, true) ||
                                        expenses.expensesRemarks.contains(searchText, true) ||
                                        expenses.createdAt.contains(searchText, true) ||
                                        expenses.updatedAt?.contains(searchText, true) == true
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