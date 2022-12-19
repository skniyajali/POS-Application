package com.niyaj.popos.realm.employee_salary.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.realm.employee_salary.domain.use_cases.SalaryUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalaryViewModel @Inject constructor(
    private val salaryUseCases: SalaryUseCases
): ViewModel(){

    private val _salaries = MutableStateFlow(SalaryState())
    val salaries = _salaries.asStateFlow()

    private val _selectedSalary =  MutableStateFlow("")
    val selectedSalary = _selectedSalary.asStateFlow()

    private val _selectedEmployee =  MutableStateFlow("")
    val selectedEmployee = _selectedEmployee.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    init {
        getAllSalaries()
    }

    fun onEvent(event: SalaryEvent) {
        when(event){
            is SalaryEvent.SelectSalary -> {
                viewModelScope.launch {
                    if(_selectedSalary.value.isNotEmpty() && _selectedSalary.value == event.salaryId){
                        _selectedSalary.emit("")
                    }else{
                        _selectedSalary.emit(event.salaryId)
                    }
                }
            }

            is SalaryEvent.SelectEmployee -> {
                viewModelScope.launch {
                    if(_selectedEmployee.value.isNotEmpty() && _selectedEmployee.value == event.employeeId){
                        _selectedEmployee.emit("")
                    }else{
                        _selectedEmployee.emit(event.employeeId)
                    }
                }
            }

            is SalaryEvent.DeleteSalary -> {
                viewModelScope.launch {
                    when (val result = salaryUseCases.deleteSalary(event.salaryId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Salary deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete salary"))
                        }
                    }
                }
                _selectedSalary.value = ""
            }

            is SalaryEvent.OnSearchSalary -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    getAllSalaries(searchText = event.searchText)
                }
            }

            is SalaryEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is SalaryEvent.RefreshSalary -> {
                getAllSalaries()
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
            getAllSalaries()
        }
    }

    private fun getAllSalaries(searchText: String = "") {
        viewModelScope.launch {
          salaryUseCases.getAllSalary(searchText).collect { result ->
              when(result){
                  is Resource.Loading -> {
                      _salaries.value = _salaries.value.copy(
                          isLoading = result.isLoading
                      )
                  }
                  is Resource.Success -> {
                      result.data?.let {
                          _salaries.value = _salaries.value.copy(
                              salary = it
                          )
                      }
                  }
                  is Resource.Error -> {
                      _salaries.value = _salaries.value.copy(
                        hasError = result.message
                      )
                  }
              }
          }
        }
    }
}