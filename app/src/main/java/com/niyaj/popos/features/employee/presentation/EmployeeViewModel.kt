package com.niyaj.popos.features.employee.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.SortType
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.domain.util.FilterEmployee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeUseCases: EmployeeUseCases,
): ViewModel() {

    private val _state = MutableStateFlow(EmployeeState())
    val state = _state.asStateFlow()

    private val _selectedEmployee =  MutableStateFlow("")
    val selectedEmployee = _selectedEmployee.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    init {
        getAllEmployees(FilterEmployee.ByEmployeeId(SortType.Descending))
    }

    fun onEmployeeEvent(event: EmployeeEvent) {
        when (event){

            is EmployeeEvent.SelectEmployee -> {
                viewModelScope.launch {
                    if(_selectedEmployee.value.isNotEmpty() && _selectedEmployee.value == event.employeeId){
                        _selectedEmployee.emit("")
                    }else{
                        _selectedEmployee.emit(event.employeeId)
                    }
                }
            }

            is EmployeeEvent.DeleteEmployee -> {
                viewModelScope.launch {
                    when (val result = employeeUseCases.deleteEmployee(event.employeeId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Employee deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete employee"))
                        }
                    }
                }
                _selectedEmployee.value = ""
            }

            is EmployeeEvent.OnFilterEmployee -> {
                if(_state.value.filterEmployee::class == event.filterEmployee::class &&
                    _state.value.filterEmployee.sortType == event.filterEmployee.sortType
                ){
                    _state.value = _state.value.copy(
                        filterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending)
                    )
                    return
                }

                _state.value = _state.value.copy(
                    filterEmployee = event.filterEmployee
                )

                getAllEmployees(event.filterEmployee)
            }

            is EmployeeEvent.OnSearchEmployee -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    getAllEmployees(
                        _state.value.filterEmployee,
                        searchText = event.searchText
                    )
                }
            }

            is EmployeeEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is EmployeeEvent.RefreshEmployee -> {
                getAllEmployees(
                    _state.value.filterEmployee
                )
            }
        }
    }

    private fun getAllEmployees(filterEmployee: FilterEmployee, searchText: String = "") {
        viewModelScope.launch {
            employeeUseCases.getAllEmployee(filterEmployee, searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                employees = it,
                                filterEmployee = filterEmployee
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                        _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
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
            getAllEmployees(
                _state.value.filterEmployee,
                _searchText.value
            )
        }
    }
}