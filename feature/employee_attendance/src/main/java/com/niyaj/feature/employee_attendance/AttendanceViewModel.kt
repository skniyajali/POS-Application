package com.niyaj.feature.employee_attendance

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.AttendanceRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val absents = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            repository.getAllAttendance(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.attendeeId }

                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    private val _selectedEmployee = MutableStateFlow("")
    val selectedEmployee = _selectedEmployee.asStateFlow()

    fun selectEmployee(employeeId: String) {
        viewModelScope.launch {
            if (_selectedEmployee.value == employeeId) {
                _selectedEmployee.value = ""
            } else {
                _selectedEmployee.value = employeeId
            }
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = repository.removeAttendances(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.Success("${selectedItems.size} absentees has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }
}