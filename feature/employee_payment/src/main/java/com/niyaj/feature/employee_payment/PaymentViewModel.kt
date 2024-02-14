package com.niyaj.feature.employee_payment

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.PaymentRepository
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

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    private val _selectedEmployee = MutableStateFlow("")
    val selectedEmployee = _selectedEmployee.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val payments = snapshotFlow { mSearchText.value }
        .flatMapLatest { it ->
            paymentRepository.getAllPayments(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.paymentId }

                    if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = paymentRepository.deletePayments(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error(result.message ?: "Unable"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.Success("${selectedItems.size} payments has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }

    fun selectEmployee(employeeId: String) {
        viewModelScope.launch {
            if (_selectedEmployee.value == employeeId) {
                _selectedEmployee.value = ""
            } else {
                _selectedEmployee.value = employeeId
            }
        }
    }
}