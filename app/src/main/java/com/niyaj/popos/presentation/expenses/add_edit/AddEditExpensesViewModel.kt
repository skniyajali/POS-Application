package com.niyaj.popos.presentation.expenses.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.use_cases.expenses.ExpensesUseCases
import com.niyaj.popos.domain.use_cases.expenses.validation.ValidateExpansesCategory
import com.niyaj.popos.domain.use_cases.expenses.validation.ValidateExpansesPrice
import com.niyaj.popos.domain.use_cases.expenses_category.ExpensesCategoryUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.domain.util.filter_items.FilterExpensesCategory
import com.niyaj.popos.presentation.expenses_category.ExpensesCategoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpensesViewModel @Inject constructor(
    private val validateExpensesCategory: ValidateExpansesCategory,
    private val validateExpensesPrice: ValidateExpansesPrice,
    private val expensesUseCases: ExpensesUseCases,
    private val expensesCategoryUseCases: ExpensesCategoryUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _addEditState = MutableStateFlow(AddEditExpensesState())
    val addEditState = _addEditState.asStateFlow()

    private val _expensesCategories = MutableStateFlow(ExpensesCategoryState())
    val expensesCategories = _expensesCategories.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("expensesId")?.let { expensesId ->
            getExpensesById(expensesId)
        }

        getAllExpensesCategory(FilterExpensesCategory.ByExpensesCategoryId(SortType.Descending))
    }

    fun onExpensesEvent(event: AddEditExpensesEvent) {
        when(event) {
            is AddEditExpensesEvent.ExpensesCategoryNameChanged -> {
                viewModelScope.launch {
                    val result = expensesCategoryUseCases.getExpensesCategoryById(event.expensesCategoryId).data

                    _addEditState.value = _addEditState.value.copy(
                        expensesCategory = result!!
                    )
                }
            }

            is AddEditExpensesEvent.ExpensesPriceChanged -> {
                _addEditState.value = _addEditState.value.copy(expensesPrice = event.expensesPrice)
            }

            is AddEditExpensesEvent.ExpensesRemarksChanged -> {
                _addEditState.value = _addEditState.value.copy(expensesRemarks = event.expensesRemarks)
            }

            is AddEditExpensesEvent.CreateNewExpenses -> {
                addOrEditExpenses()
            }

            is AddEditExpensesEvent.UpdateExpenses -> {
                addOrEditExpenses(event.expensesId)
            }

            is AddEditExpensesEvent.OnSearchExpensesCategory -> {
                viewModelScope.launch {
                    getAllExpensesCategory(
                        _expensesCategories.value.filterExpensesCategory,
                        searchText = event.searchText
                    )
                }
            }
        }
    }

    private fun addOrEditExpenses(expensesId: String? = null){
        val validatedExpensesCategory = validateExpensesCategory.execute(_addEditState.value.expensesCategory.expensesCategoryId)

        val validatedExpensesPrice = validateExpensesPrice.execute(_addEditState.value.expensesPrice)

        val hasError = listOf(validatedExpensesCategory, validatedExpensesPrice).any {
            !it.successful
        }

        if (hasError) {
            _addEditState.value = addEditState.value.copy(
                expensesCategoryError = validatedExpensesCategory.errorMessage,
                expensesPriceError = validatedExpensesPrice.errorMessage,
            )
            return
        }else {
            viewModelScope.launch {
                if(expensesId.isNullOrEmpty()){
                    val result = expensesUseCases.createNewExpenses(
                        Expenses(
                            expensesCategory = _addEditState.value.expensesCategory,
                            expansesPrice = _addEditState.value.expensesPrice,
                            expansesRemarks = _addEditState.value.expensesRemarks,
                        )
                    )
                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Expenses created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new expenses"))
                        }
                    }

                }else {
                    val result = expensesUseCases.updateExpenses(
                        Expenses(
                            expensesCategory = _addEditState.value.expensesCategory,
                            expansesPrice = _addEditState.value.expensesPrice,
                            expansesRemarks = _addEditState.value.expensesRemarks,
                        ),
                        expensesId
                    )
                    when(result){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError( "Unable to Update Expenses"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses updated successfully"))
                        }
                    }
                }
            }

            _addEditState.value = AddEditExpensesState()
        }
    }

    private fun getExpensesById(expensesId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = expensesUseCases.getExpensesById(expensesId)) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    result.data?.let {
                        _addEditState.value = addEditState.value.copy(
                            expensesCategory = it.expensesCategory,
                            expensesPrice = it.expansesPrice,
                            expensesRemarks = it.expansesRemarks
                        )
                    }
                }

                is Resource.Error -> {}
            }
        }
    }

    private fun getAllExpensesCategory(filterExpensesCategory: FilterExpensesCategory, searchText: String = "") {
        viewModelScope.launch {
            expensesCategoryUseCases.getAllExpensesCategory(filterExpensesCategory, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _expensesCategories.value = _expensesCategories.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _expensesCategories.value = _expensesCategories.value.copy(
                                expensesCategory = it,
                                filterExpensesCategory = filterExpensesCategory
                            )
                        }
                    }
                    is Resource.Error -> {
                        _expensesCategories.value = _expensesCategories.value.copy(error = "Unable to load resources")
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

}