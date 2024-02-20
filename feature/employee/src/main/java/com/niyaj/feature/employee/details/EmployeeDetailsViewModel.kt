package com.niyaj.feature.employee.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.model.EmployeeAbsentDates
import com.niyaj.model.EmployeePayments
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Employee Details view model class
 *
 */
@HiltViewModel
class EmployeeDetailsViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _employeeId = savedStateHandle.get<String>("employeeId") ?: ""

    private val _selectedSalaryDate = mutableStateOf<Pair<String, String>?>(null)
    val selectedSalaryDate: State<Pair<String, String>?> = _selectedSalaryDate

    val employeeDetails = snapshotFlow { _employeeId }.mapLatest {
        val resource = employeeRepository.getEmployeeById(it)

        if (resource == null) UiState.Empty else UiState.Success(resource)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val salaryDates = snapshotFlow { _employeeId }.mapLatest {
        employeeRepository.getEmployeeMonthlyDate(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
    )

    val salaryEstimation = snapshotFlow { _selectedSalaryDate.value }.flatMapLatest { date ->
        employeeRepository.getEmployeeSalaryEstimation(_employeeId, date)
    }.mapLatest { data ->
        UiState.Success(data)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    val payments = MutableStateFlow<UiState<List<EmployeePayments>>>(UiState.Loading)

    val employeeAbsentDates = MutableStateFlow<UiState<List<EmployeeAbsentDates>>>(UiState.Loading)

    init {
        savedStateHandle.get<String>("employeeId")?.let {
            getEmployeePayments(it)
            getEmployeeAbsentDates(it)
        }
    }

    fun onEvent(event: EmployeeDetailsEvent) {
        when (event) {
            is EmployeeDetailsEvent.OnChooseSalaryDate -> {
                _selectedSalaryDate.value = event.date
            }

            is EmployeeDetailsEvent.RefreshData -> {
                getEmployeePayments(_employeeId)
                getEmployeeAbsentDates(_employeeId)
            }
        }
    }

    private fun getEmployeePayments(employeeId: String) {
        viewModelScope.launch {
            employeeRepository.getEmployeePayments(employeeId).collectLatest { result ->
                payments.update {
                    if (result.isEmpty()) UiState.Empty else UiState.Success(result)
                }
            }
        }
    }

    private fun getEmployeeAbsentDates(employeeId: String) {
        viewModelScope.launch {
            employeeRepository.getEmployeeAbsentDates(employeeId).collectLatest { result ->
                employeeAbsentDates.update {
                    if (result.isEmpty()) UiState.Empty else UiState.Success(result)
                }
            }
        }
    }
}