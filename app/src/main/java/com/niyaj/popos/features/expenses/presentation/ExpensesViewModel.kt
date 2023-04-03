package com.niyaj.popos.features.expenses.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses.domain.use_cases.ExpensesUseCases
import com.niyaj.popos.features.expenses.domain.util.FilterExpenses
import com.niyaj.popos.util.*
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

    private val _state = MutableStateFlow(ExpensesState())
    val state = _state.asStateFlow()

    private val _totalState = MutableStateFlow(TotalExpensesState())
    val totalState = _totalState.asStateFlow()

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
                if(_state.value.filterExpenses::class == event.filterExpenses::class &&
                    _state.value.filterExpenses.sortType == event.filterExpenses.sortType
                ){
                    _state.value = _state.value.copy(
                        filterExpenses = FilterExpenses.ByExpensesId(SortType.Descending)
                    )
                    return
                }

                _state.value = _state.value.copy(
                    filterExpenses = event.filterExpenses
                )

                getAllExpenses(event.filterExpenses)
            }

            is ExpensesEvent.OnSearchExpenses -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllExpenses(
                        _state.value.filterExpenses,
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
                getAllExpenses(_state.value.filterExpenses)
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

            is ExpensesEvent.OnSelectDate -> {
                val startDate = getCalculatedStartDate(date = event.selectedDate)
                val endDate = getCalculatedEndDate(date = event.selectedDate)

                getAllExpenses(
                    filterExpenses = _state.value.filterExpenses,
                    searchText = _searchText.value,
                    startDate = startDate,
                    endDate = endDate
                )
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
                _state.value.filterExpenses,
                _searchText.value
            )
        }
    }

    private fun getAllExpenses(
        filterExpenses : FilterExpenses,
        searchText : String = "",
        startDate : String = getStartTime,
        endDate : String = getEndTime,
    ) {
        viewModelScope.launch {
            expensesUseCases.getAllExpenses(
                filterExpenses,
                searchText,
                startDate,
                endDate
            ).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                expenses = it,
                                filterExpenses = filterExpenses,
                            )

                            _totalState.value = _totalState.value.copy(
                                totalAmount = it.sumOf { expenses ->
                                    expenses.expensesPrice.toLong()
                                }.toString(),
                                totalPayment = it.size,
                                selectedDate = startDate.toFormattedDate
                            )
                        }
                    }
                    is Resource.Error -> {
                        withContext(Dispatchers.Main){
                            _state.value = _state.value.copy(error = "Unable to load resources")
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                        }
                    }
                }
            }
        }
    }

}