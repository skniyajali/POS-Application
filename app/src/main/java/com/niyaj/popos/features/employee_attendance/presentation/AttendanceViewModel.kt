package com.niyaj.popos.features.employee_attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceUseCases: AttendanceUseCases,
    private val employeeUseCases : EmployeeUseCases,
): ViewModel() {

    private val _attendance = MutableStateFlow(AttendanceState())
    val attendance = _attendance.asStateFlow()

    private val _selectedAttendance = MutableStateFlow("")
    val selectedAttendance = _selectedAttendance.asStateFlow()

    private val _selectedEmployee =  MutableStateFlow("")
    val selectedEmployee = _selectedEmployee.asStateFlow()

    private val _searchText =  MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _toggledSearchBar = MutableStateFlow(false)
    val toggledSearchBar = _toggledSearchBar.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        getAllAttendance()
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.RefreshAttendance -> {
                getAllAttendance()
            }

            is AttendanceEvent.SelectAttendance -> {
                viewModelScope.launch {
                    if(_selectedAttendance.value.isNotEmpty() && _selectedAttendance.value == event.attendanceId){
                        _selectedAttendance.emit("")
                    }else{
                        _selectedAttendance.emit(event.attendanceId)
                    }
                }
            }

            is AttendanceEvent.SelectEmployee -> {
                viewModelScope.launch {
                    if(_selectedEmployee.value.isNotEmpty() && _selectedEmployee.value == event.employeeId){
                        _selectedEmployee.emit("")
                    }else{
                        _selectedEmployee.emit(event.employeeId)
                    }
                }
            }

            is AttendanceEvent.OnSearchAttendance -> {
                viewModelScope.launch {
                    _searchText.emit(event.searchText)

                    getAllAttendance(event.searchText)
                }
            }

            is AttendanceEvent.ToggleSearchBar -> {
                viewModelScope.launch {
                    _toggledSearchBar.emit(!_toggledSearchBar.value)
                }
            }

            is AttendanceEvent.DeleteAttendance -> {
                viewModelScope.launch {

                    when (val result = attendanceUseCases.deleteAttendanceById(event.attendanceId)) {
                        is Resource.Loading -> {
                            _eventFlow.emit(UiEvent.IsLoading(result.isLoading))
                        }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Employee Absent deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete employee absent"))
                        }
                    }

                    _selectedAttendance.value = ""
                }
            }
        }
    }

    private fun getAllAttendance(searchText: String = "") {
        viewModelScope.launch {
            attendanceUseCases.getAllAttendance(searchText).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        _attendance.value = _attendance.value.copy(
                            isLoading = result.isLoading
                        )
                    }
                    is Resource.Success -> {
                        result.data?.let {
                            _attendance.value = _attendance.value.copy(
                                attendances = it
                            )
                        }

                    }
                    is Resource.Error -> {
                        _attendance.value = _attendance.value.copy(
                            error = result.message
                        )
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
            getAllAttendance()
        }
    }

    fun getEmployeeById(employeeId: String): Employee? {
        return employeeUseCases.getEmployeeById(employeeId).data
    }

}