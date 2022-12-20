package com.niyaj.popos.features.expenses.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses.domain.use_cases.ExpensesUseCases
import com.niyaj.popos.features.expenses.domain.util.FilterExpenses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesUseCases: ExpensesUseCases,
): ViewModel() {

    var state by mutableStateOf(ExpensesState())

    private val _selectedExpenses =  MutableStateFlow("")
    val selectedExpenses = _selectedExpenses.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    init {
        getAllExpenses(FilterExpenses.ByExpensesCategory(SortType.Descending))
    }

    fun onExpensesEvent(event: ExpensesEvent) {
        when (event){

            is ExpensesEvent.SelectExpenses -> {
                viewModelScope.launch {
                    if(_selectedExpenses.value.isNotEmpty() && _selectedExpenses.value == event.expensesId){
                        _selectedExpenses.emit("")
                    }else{
                        _selectedExpenses.emit(event.expensesId)
                    }
                }
            }

            is ExpensesEvent.DeleteExpenses -> {
                viewModelScope.launch {
                    when (val result = expensesUseCases.deleteExpenses(event.expensesId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete employee"))
                        }
                    }
                }
                _selectedExpenses.value = ""
            }

            is ExpensesEvent.OnFilterExpenses -> {
                if(state.filterExpenses::class == event.filterExpenses::class &&
                    state.filterExpenses.sortType == event.filterExpenses.sortType
                ){
                    state = state.copy(
                        filterExpenses = FilterExpenses.ByExpensesId(SortType.Descending)
                    )
                    return
                }

                state = state.copy(
                    filterExpenses = event.filterExpenses
                )

                getAllExpenses(event.filterExpenses)
            }

            is ExpensesEvent.OnSearchExpenses -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllExpenses(
                        state.filterExpenses,
                        searchText = event.searchText
                    )
                }
            }

            is ExpensesEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ExpensesEvent.RefreshExpenses -> {
                getAllExpenses(state.filterExpenses)
            }

            is ExpensesEvent.DeleteAllExpenses -> {
                viewModelScope.launch {
                    when(val result = expensesUseCases.deletePastExpenses(deleteAll = true)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("All Expenses has been deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete expenses"))
                        }
                    }
                }
            }

            is ExpensesEvent.DeletePastExpenses -> {
                viewModelScope.launch {
                    when(val result = expensesUseCases.deletePastExpenses(deleteAll = false)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses has been deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete expenses"))
                        }
                    }
                }
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
            getAllExpenses(
                state.filterExpenses,
                _searchText.value
            )
        }
    }

    private fun getAllExpenses(filterExpenses: FilterExpenses, searchText: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            expensesUseCases.getAllExpenses(filterExpenses, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            state = state.copy(
                                expenses = it,
                                filterExpenses = filterExpenses
                            )
                        }
                    }
                    is Resource.Error -> {
                        withContext(Dispatchers.Main){
                            state = state.copy(error = "Unable to load resources")
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                        }
                    }
                }
            }
        }
    }

}