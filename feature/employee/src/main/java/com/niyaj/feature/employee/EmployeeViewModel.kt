package com.niyaj.feature.employee

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Employee screen
 * @constructor [EmployeeRepository]
 */
@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val employeeRepository: EmployeeRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    val employees = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            employeeRepository.getAllEmployee(it)
        }.mapLatest { items ->
            totalItems = items.map { it.employeeId }
            if (items.isEmpty()) UiState.Empty else UiState.Success(items)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (employeeRepository.deleteEmployees(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error("Unable to delete employee"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success(
                            "${selectedItems.size} employee has been deleted"
                        )
                    )
                }
            }
            mSelectedItems.clear()
        }
    }

}