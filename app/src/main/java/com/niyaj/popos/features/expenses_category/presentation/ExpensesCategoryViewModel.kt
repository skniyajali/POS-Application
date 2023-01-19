package com.niyaj.popos.features.expenses_category.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.use_cases.ExpensesCategoryUseCases
import com.niyaj.popos.features.expenses_category.domain.util.FilterExpensesCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesCategoryViewModel @Inject constructor(
    private val expensesCategoryUseCases: ExpensesCategoryUseCases,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _expensesCategories = MutableStateFlow(ExpensesCategoryState())
    val expensesCategories = _expensesCategories.asStateFlow()

    var addEditState by mutableStateOf(AddEditExpensesCategoryState())

    private val _selectedExpensesCategory =  MutableStateFlow("")
    val selectedExpensesCategory = _selectedExpensesCategory.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    var expanded by mutableStateOf(false)

    init {
        getAllExpensesCategory(FilterExpensesCategory.ByExpensesCategoryId(SortType.Descending))

        savedStateHandle.get<String>("expensesCategoryId")?.let { expensesCategoryId ->
            getExpensesCategoryById(expensesCategoryId)
        }
    }

    fun onExpensesCategoryEvent(event: ExpensesCategoryEvent) {
        when (event){

            is ExpensesCategoryEvent.ExpensesCategoryNameChanged -> {
                addEditState = addEditState.copy(expensesCategoryName = event.expensesCategoryName)
            }

            is ExpensesCategoryEvent.SelectExpensesCategory -> {
                viewModelScope.launch {
                    if(_selectedExpensesCategory.value.isNotEmpty() && _selectedExpensesCategory.value == event.expensesCategoryId){
                        _selectedExpensesCategory.emit("")
                    }else{
                        _selectedExpensesCategory.emit(event.expensesCategoryId)
                    }
                }
            }

            is ExpensesCategoryEvent.CreateNewExpensesCategory -> {
                addOrEditExpensesCategory()
            }

            is ExpensesCategoryEvent.UpdateExpensesCategory -> {
                addOrEditExpensesCategory(event.expensesCategoryId)
            }

            is ExpensesCategoryEvent.DeleteExpensesCategory -> {
                viewModelScope.launch {
                    when (val result = expensesCategoryUseCases.deleteExpensesCategory(event.expensesCategoryId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses Category deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete Expenses Category"))
                        }
                    }
                }
                _selectedExpensesCategory.value = ""
            }

            is ExpensesCategoryEvent.OnFilterExpensesCategory -> {
                if(_expensesCategories.value.filterExpensesCategory::class == event.filterExpensesCategory::class &&
                    _expensesCategories.value.filterExpensesCategory.sortType == event.filterExpensesCategory.sortType
                ){
                    _expensesCategories.value = _expensesCategories.value.copy(
                        filterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(
                            SortType.Descending)
                    )
                    return
                }

                _expensesCategories.value = _expensesCategories.value.copy(
                    filterExpensesCategory = event.filterExpensesCategory
                )

                getAllExpensesCategory(event.filterExpensesCategory)
            }

            is ExpensesCategoryEvent.OnSearchExpensesCategory -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllExpensesCategory(
                        _expensesCategories.value.filterExpensesCategory,
                        searchText = event.searchText
                    )
                }
            }

            is ExpensesCategoryEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ExpensesCategoryEvent.RefreshExpenses -> {
                getAllExpensesCategory(_expensesCategories.value.filterExpensesCategory)
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

    private fun addOrEditExpensesCategory(expensesCategoryId: String? = null){
        val validatedExpensesCategoryName = expensesCategoryUseCases.validateExpensesCategoryName(addEditState.expensesCategoryName)

        val hasError = listOf(validatedExpensesCategoryName).any {
            !it.successful
        }

        if (hasError) {
            addEditState = addEditState.copy(
                expensesCategoryNameError = validatedExpensesCategoryName.errorMessage,
            )

            return
        }else {
            viewModelScope.launch {
                if(expensesCategoryId.isNullOrEmpty()){
                    val result = expensesCategoryUseCases.createNewExpensesCategory(
                        ExpensesCategory(
                            expensesCategoryName = addEditState.expensesCategoryName,
                        )
                    )
                    when(result){
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess(result.message ?: "Expenses Category created successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to create new Expenses Category"))
                        }
                    }

                }else {

                    val result = expensesCategoryUseCases.updateExpensesCategory(
                        ExpensesCategory(
                            expensesCategoryName = addEditState.expensesCategoryName,
                        ),
                        expensesCategoryId
                    )
                    when(result){
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError( "Unable to Update Expenses Category"))
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses Category updated successfully"))
                        }
                    }
                }
            }

            addEditState = AddEditExpensesCategoryState()
            _selectedExpensesCategory.value = ""
        }
    }

    private fun getExpensesCategoryById(expensesCategoryId: String) {
        viewModelScope.launch {
            when(val result = expensesCategoryUseCases.getExpensesCategoryById(expensesCategoryId)) {
                is Resource.Loading -> {}

                is Resource.Success -> {
                    result.data?.let {
                        addEditState = addEditState.copy(
                            expensesCategoryName = it.expensesCategoryName,
                        )
                    }
                }
                is Resource.Error -> {}
            }
        }
    }

    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllExpensesCategory(
                _expensesCategories.value.filterExpensesCategory,
                _searchText.value
            )
        }
    }
}