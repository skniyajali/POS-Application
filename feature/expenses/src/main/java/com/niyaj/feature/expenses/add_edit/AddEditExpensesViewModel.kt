package com.niyaj.feature.expenses.add_edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.capitalizeWords
import com.niyaj.data.repository.ExpensesCategoryRepository
import com.niyaj.data.repository.ExpensesRepository
import com.niyaj.data.repository.validation.ExpensesValidationRepository
import com.niyaj.model.Expenses
import com.niyaj.model.ExpensesCategory
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class AddEditExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
    private val validationRepository: ExpensesValidationRepository,
    private val expensesCategoryRepository: ExpensesCategoryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var state by mutableStateOf(AddEditExpenseState())

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _name = snapshotFlow { state.expensesCategory }
    private val _date = snapshotFlow { state.expenseDate }

    init {
        savedStateHandle.get<String>("expensesId")?.let { expenseId ->
            getExpenseById(expenseId)
        }
    }

    val nameError: StateFlow<String?> = _name.mapLatest {
        validationRepository.validateExpensesCategory(it.expensesCategoryName).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val priceError: StateFlow<String?> = snapshotFlow { state.expenseAmount }
        .mapLatest {
            validationRepository.validateExpensesPrice(it).errorMessage
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val dateError: StateFlow<String?> = _date.mapLatest {
        validationRepository.validateExpenseDate(it).errorMessage
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val expensesName = snapshotFlow { state.expensesCategory.expensesCategoryName }
        .flatMapLatest { _ ->
            expensesCategoryRepository.getAllExpensesCategory("")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun onEvent(event: AddEditExpenseEvent) {
        when (event) {
            is AddEditExpenseEvent.ExpensesNameChanged -> {
                state = state.copy(expensesCategory = event.expenseName)
            }

            is AddEditExpenseEvent.ExpensesAmountChanged -> {
                state = state.copy(expenseAmount = event.expenseAmount)
            }

            is AddEditExpenseEvent.ExpensesDateChanged -> {
                state = state.copy(expenseDate = event.expenseDate)
            }

            is AddEditExpenseEvent.ExpensesNoteChanged -> {
                state = state.copy(expenseNote = event.expenseNote)
            }

            is AddEditExpenseEvent.AddOrUpdateExpense -> {
                addOrUpdateExpense(event.expenseId)
            }
        }
    }

    private fun getExpenseById(expenseId: String) {
        viewModelScope.launch {
            when (val result = expensesRepository.getExpensesById(expenseId)) {
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error("Unable to find expense"))
                }

                is Resource.Success -> {
                    result.data?.let { expense ->
                        state = state.copy(
                            expensesCategory = expense.expensesCategory ?: ExpensesCategory(),
                            expenseDate = expense.expensesDate,
                            expenseAmount = expense.expensesAmount,
                            expenseNote = expense.expensesRemarks
                        )
                    }
                }
            }
        }
    }

    private fun addOrUpdateExpense(expenseId: String = "") {
        viewModelScope.launch {
            val hasError = listOf(nameError, dateError, priceError).all { it.value != null }

            if (!hasError) {
                val newExpense = Expenses(
                    expensesId = expenseId,
                    expensesCategory = state.expensesCategory,
                    expensesAmount = state.expenseAmount,
                    expensesDate = state.expenseDate,
                    expensesRemarks = state.expenseNote.trim().capitalizeWords,
                    createdAt = System.currentTimeMillis().toString(),
                    updatedAt = if (expenseId.isNotEmpty()) System.currentTimeMillis()
                        .toString() else null
                )

                val message = if (expenseId.isEmpty()) "created" else "updated"

                val result = expensesRepository.createOrUpdateExpenses(newExpense, expenseId)

                when (result) {
                    is Resource.Error -> {
                        _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to add expense"))
                    }

                    is Resource.Success -> {
                        _eventFlow.emit(UiEvent.Success("Expense $message successfully"))
                    }
                }

                state = AddEditExpenseState()
            }
        }
    }

}