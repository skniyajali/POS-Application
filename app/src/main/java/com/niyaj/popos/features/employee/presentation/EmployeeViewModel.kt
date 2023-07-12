package com.niyaj.popos.features.employee.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee.domain.use_cases.GetAllEmployee
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Employee screen
 * @constructor [EmployeeRepository] & [GetAllEmployee]
 */
@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    private val getAllEmployee : GetAllEmployee,
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
        getAllEmployees()
    }

    /**
     * Handle all the events from the screen
     * @param event
     * @see EmployeeEvent
     */
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
                    when (val result = employeeRepository.deleteEmployee(event.employeeId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Employee deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete employee"))
                        }
                    }
                }
                _selectedEmployee.value = ""
            }

            is EmployeeEvent.OnSearchEmployee -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    getAllEmployees(searchText = event.searchText)
                }
            }

            is EmployeeEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is EmployeeEvent.RefreshEmployee -> {
                getAllEmployees()
            }
        }
    }

    private fun getAllEmployees(searchText: String = "") {
        viewModelScope.launch {
            getAllEmployee.invoke(searchText).collect{ result ->
                when(result){
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _state.value = _state.value.copy(
                                employees = it
                            )
                        }
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(error = result.message)
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to load resources"))
                    }
                }
            }
        }
    }

    /**
     * On click to close the search bar and clear the search text and emit empty string
     */
    fun onSearchBarCloseAndClearClick(){
        viewModelScope.launch {
            onSearchTextClearClick()

            _toggledSearchBar.emit(false)

        }
    }

    /**
     * Clear search text and emit empty string
     *
     */
    fun onSearchTextClearClick(){
        viewModelScope.launch {
            _searchText.emit("")
         }
    }
}