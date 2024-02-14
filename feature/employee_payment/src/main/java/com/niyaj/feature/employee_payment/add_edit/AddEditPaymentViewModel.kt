package com.niyaj.feature.employee_payment.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import com.niyaj.model.Employee
import com.niyaj.model.Payment
import com.niyaj.model.PaymentMode
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPaymentViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val validationRepository: PaymentValidationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val paymentId = savedStateHandle.get<String>("paymentId") ?: ""

    var state by mutableStateOf(AddEditPaymentState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val employees = snapshotFlow { paymentId }.flatMapLatest {
        paymentRepository.getAllEmployee()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedEmployee = MutableStateFlow(Employee())
    val selectedEmployee = _selectedEmployee.asStateFlow()

    init {
        savedStateHandle.get<String>("paymentId")?.let { paymentId ->
            getPaymentById(paymentId)
        }

        savedStateHandle.get<String>("employeeId")?.let { employeeId ->
            getEmployeeById(employeeId)
        }
    }

    val employeeError: StateFlow<String?> = _selectedEmployee
        .mapLatest {
            validationRepository.validateEmployee(it.employeeId).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val amountError: StateFlow<String?> = snapshotFlow { state.paymentAmount }
        .mapLatest {
            validationRepository.validatePaymentAmount(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val dateError: StateFlow<String?> = snapshotFlow { state.paymentDate }
        .mapLatest {
            validationRepository.validateGivenDate(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentTypeError: StateFlow<String?> = snapshotFlow { state.paymentType }
        .mapLatest {
            validationRepository.validatePaymentType(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentModeError: StateFlow<String?> = snapshotFlow { state.paymentMode }
        .mapLatest {
            validationRepository.validatePaymentMode(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val paymentNoteError: StateFlow<String?> = snapshotFlow { state.paymentMode }.combine(
        snapshotFlow { state.paymentNote }) { mode, note ->
        validationRepository.validatePaymentNote(
            paymentNote = note,
            isRequired = mode == PaymentMode.Both
        ).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    fun onEvent(event: AddEditPaymentEvent) {
        when (event) {
            is AddEditPaymentEvent.PaymentAmountChanged -> {
                state = state.copy(paymentAmount = event.paymentAmount)
            }

            is AddEditPaymentEvent.PaymentDateChanged -> {
                state = state.copy(paymentDate = event.paymentDate)
            }

            is AddEditPaymentEvent.PaymentModeChanged -> {
                state = state.copy(paymentMode = event.paymentMode)
            }

            is AddEditPaymentEvent.PaymentNoteChanged -> {
                state = state.copy(paymentNote = event.paymentNote)
            }

            is AddEditPaymentEvent.PaymentTypeChanged -> {
                state = state.copy(paymentType = event.paymentType)
            }

            is AddEditPaymentEvent.CreateOrUpdatePayment -> {
                createOrUpdatePayment(paymentId)
            }

            is AddEditPaymentEvent.OnSelectEmployee -> {
                viewModelScope.launch {
                    _selectedEmployee.value = event.employee
                }
            }
        }
    }

    private fun getPaymentById(itemId: String) {
        viewModelScope.launch {
            when (val result = paymentRepository.getPaymentById(itemId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to find payment"))
                }

                is Resource.Success -> {
                    result.data?.let { payment ->
                        getEmployeeById(payment.employee?.employeeId ?: "")

                        state = state.copy(
                            paymentAmount = payment.paymentAmount,
                            paymentDate = payment.paymentDate,
                            paymentMode = payment.paymentMode,
                            paymentType = payment.paymentType,
                            paymentNote = payment.paymentNote,
                        )
                    }
                }
            }
        }
    }

    private fun getEmployeeById(employeeId: String) {
        viewModelScope.launch {
            paymentRepository.getEmployeeById(employeeId)?.let { employee ->
                _selectedEmployee.value = employee
            }
        }
    }

    private fun createOrUpdatePayment(paymentId: String = "") {
        viewModelScope.launch {
            val hasError = listOf(
                employeeError,
                amountError,
                dateError,
                paymentModeError,
                paymentNoteError,
                paymentTypeError
            ).all { it.value != null }

            if (!hasError) {
                val newPayment = Payment(
                    paymentId = paymentId,
                    employee = _selectedEmployee.value,
                    paymentAmount = state.paymentAmount,
                    paymentDate = state.paymentDate,
                    paymentType = state.paymentType,
                    paymentMode = state.paymentMode,
                    paymentNote = state.paymentNote,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (paymentId.isEmpty()) System.currentTimeMillis()
                        .toString() else null
                )

                if (paymentId.isEmpty()) {
                    when (paymentRepository.addNewPayment(newPayment)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Add Payment."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Payment Created Successfully."))
                        }
                    }
                } else {
                    when (paymentRepository.updatePaymentById(newPayment, paymentId)) {
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.Error("Unable To Add Payment."))
                        }

                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Payment Updated Successfully."))
                        }
                    }
                }

                state = AddEditPaymentState()
            }
        }
    }
}