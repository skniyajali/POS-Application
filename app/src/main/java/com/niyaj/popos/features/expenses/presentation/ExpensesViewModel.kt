package com.niyaj.popos.features.expenses.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.getCalculatedEndDate
import com.niyaj.popos.common.utils.getCalculatedStartDate
import com.niyaj.popos.common.utils.isToday
import com.niyaj.popos.common.utils.toPrettyDate
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.use_cases.GetAllExpenses
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

/**
 *
 */
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    private val getAllExpenses : GetAllExpenses,
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
        getAllExpenses()
    }

    /**
     *
     */
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
                    when (val result = expensesRepository.deleteExpenses(event.expensesId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Expenses deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete employee"))
                        }
                    }
                }
                _selectedExpenses.value = ""
            }

            is ExpensesEvent.OnSearchExpenses -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    delay(500L)
                    getAllExpenses(searchText = event.searchText)
                }
            }

            is ExpensesEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is ExpensesEvent.RefreshExpenses -> {
                getAllExpenses()
            }

            is ExpensesEvent.OnSelectDate -> {
                val startDate = getCalculatedStartDate(date = event.selectedDate)
                val endDate = getCalculatedEndDate(date = event.selectedDate)

                getAllExpenses(
                    searchText = _searchText.value,
                    startDate = startDate,
                    endDate = endDate
                )
            }
        }
    }

    /**
     *
     */
    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()
            _toggledSearchBar.emit(false)
        }
    }

    /**
     *
     */
    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
            getAllExpenses(_searchText.value)
        }
    }

    private fun getAllExpenses(
        searchText : String = "",
        startDate : String? = null,
        endDate : String? = null,
    ) {
        viewModelScope.launch {
            getAllExpenses.invoke(searchText, startDate, endDate).collect { result ->
                when(result){
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let { expenses ->
                            _state.value = _state.value.copy(expenses = expenses)

                            val totalState = if (startDate.isNullOrEmpty() && endDate.isNullOrEmpty()) {
                                expenses.filter { it.createdAt.isToday }
                            }else {
                                expenses
                            }

                            _totalState.value = _totalState.value.copy(
                                totalAmount = totalState.sumOf { expense ->
                                    expense.expensesPrice.toLong()
                                }.toString(),
                                totalPayment = totalState.size,
                                selectedDate = startDate?.toPrettyDate() ?: "Today"
                            )
                        }
                    }
                    is Resource.Error -> {
                        withContext(Dispatchers.Main){
                            _state.value = _state.value.copy(error = "Unable to load resources")
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to load resources"))
                        }
                    }
                }
            }
        }
    }
}