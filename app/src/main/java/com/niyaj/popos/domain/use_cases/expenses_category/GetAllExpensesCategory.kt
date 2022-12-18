package com.niyaj.popos.domain.use_cases.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterExpensesCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class GetAllExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(
        filterExpansesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(SortType.Descending),
        searchText: String = ""
    ): Flow<Resource<List<ExpensesCategory>>> {
        return flow {
            expensesCategoryRepository.getAllExpensesCategory().collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { data ->
                                when(filterExpansesCategory.sortType){
                                    is SortType.Ascending -> {
                                        when(filterExpansesCategory){
                                            is FilterExpensesCategory.ByExpensesCategoryId -> { data.sortedBy { it.expensesCategoryId } }
                                            is FilterExpensesCategory.ByExpensesCategoryName -> { data.sortedBy { it.expensesCategoryName } }
                                            is FilterExpensesCategory.ByExpensesCategoryDate -> { data.sortedBy { it.createdAt } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterExpansesCategory){
                                            is FilterExpensesCategory.ByExpensesCategoryId -> { data.sortedByDescending { it.expensesCategoryId } }
                                            is FilterExpensesCategory.ByExpensesCategoryName -> { data.sortedByDescending { it.expensesCategoryName } }
                                            is FilterExpensesCategory.ByExpensesCategoryDate -> { data.sortedByDescending { it.createdAt } }
                                        }
                                    }
                                }.filter { expansesCategory ->
                                    expansesCategory.expensesCategoryName.contains(searchText, true) ||
                                    expansesCategory.createdAt?.contains(searchText, true) == true ||
                                    expansesCategory.updatedAt?.contains(searchText, true) == true
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        Timber.d("Unable to get expanses category data from repository")
                        emit(Resource.Error(result.message ?: "Unable to get expanses category data from repository"))
                    }
                }
            }
        }
    }
}