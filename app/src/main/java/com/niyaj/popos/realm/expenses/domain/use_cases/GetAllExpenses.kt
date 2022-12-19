package com.niyaj.popos.realm.expenses.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.realm.expenses.domain.util.FilterExpenses
import com.niyaj.popos.realm.expenses.domain.model.Expenses
import com.niyaj.popos.realm.expenses.domain.repository.ExpensesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get expanses data from repository"))
                    }
                }
            }
        }
    }
}