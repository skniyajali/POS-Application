package com.niyaj.feature.expenses.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Constants
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.ExpensesRepository
import com.niyaj.model.Expenses
import com.niyaj.ui.event.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpensesSettingViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<List<Expenses>>(emptyList())
    val state = _state.asStateFlow()

    private val _selectedExpenses = mutableStateListOf<String>()
    val selectedExpenses: SnapshotStateList<String> = _selectedExpenses

    private val _importExportedExpenses = MutableStateFlow<List<Expenses>>(emptyList())
    val importExportedExpenses = _importExportedExpenses.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var onChoose by mutableStateOf(false)

    private var count: Int = 0

    private var exportCount = 1

    fun onEvent(event: ExpensesSettingsEvent) {
        when (event) {

            is ExpensesSettingsEvent.SelectExpenses -> {
                viewModelScope.launch {
                    if (_selectedExpenses.contains(event.expensesId)) {
                        _selectedExpenses.remove(event.expensesId)
                    } else {
                        _selectedExpenses.add(event.expensesId)
                    }
                }
            }

            is ExpensesSettingsEvent.SelectAllExpenses -> {
                val selectCount = when (event.type) {
                    Constants.ImportExportType.IMPORT -> count++
                    Constants.ImportExportType.EXPORT -> exportCount++
                }

                val expenses = when (event.type) {
                    Constants.ImportExportType.IMPORT -> _importExportedExpenses.value
                    Constants.ImportExportType.EXPORT -> _state.value
                }

                if (expenses.isNotEmpty()) {
                    expenses.forEach { expense ->
                        if (selectCount % 2 != 0) {

                            val selectedExpense =
                                _selectedExpenses.find { it == expense.expensesId }

                            if (selectedExpense == null) {
                                _selectedExpenses.add(expense.expensesId)
                            }
                        } else {
                            _selectedExpenses.remove(expense.expensesId)
                        }
                    }
                }
            }

            is ExpensesSettingsEvent.DeselectExpenses -> {
                _selectedExpenses.clear()
            }

            is ExpensesSettingsEvent.OnChooseExpenses -> {
                onChoose = !onChoose
            }

            is ExpensesSettingsEvent.ImportExpenses -> {
                val expenses = mutableStateListOf<Expenses>()

                _selectedExpenses.forEach {
                    val data =
                        _importExportedExpenses.value.find { expense -> expense.expensesId == it }
                    if (data != null) expenses.add(data)
                }

                viewModelScope.launch {
                    when (val result = expensesRepository.importExpenses(expenses.toList())) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("${expenses.toList().size} expenses imported successfully"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.Error(
                                    result.message ?: "Unable to import expenses"
                                )
                            )
                        }
                    }
                }
            }

            is ExpensesSettingsEvent.ImportExpensesData -> {
                _importExportedExpenses.value = emptyList()

                if (event.expenses.isNotEmpty()) {
                    _importExportedExpenses.value = event.expenses

                    _selectedExpenses.addAll(event.expenses.map { it.expensesId })
                }
            }

            is ExpensesSettingsEvent.ClearImportedExpenses -> {
                _importExportedExpenses.value = emptyList()
                _selectedExpenses.clear()
                onChoose = false
            }

            is ExpensesSettingsEvent.GetExportedExpenses -> {
                viewModelScope.launch {
                    if (_selectedExpenses.isEmpty()) {
                        _importExportedExpenses.emit(_state.value)
                    } else {
                        val expenses = mutableListOf<Expenses>()

                        _selectedExpenses.forEach { id ->
                            val customer = _state.value.find { it.expensesId == id }
                            if (customer != null) {
                                expenses.add(customer)
                            }
                        }

                        _importExportedExpenses.emit(expenses.toList())
                    }
                }
            }

            is ExpensesSettingsEvent.GetAllExpenses -> {
                getAllExpenses()
            }

            is ExpensesSettingsEvent.DeleteAllExpenses -> {
                viewModelScope.launch {
                    when (val result = expensesRepository.deletePastExpenses(deleteAll = true)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("All Expenses has been deleted successfully"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.Error(
                                    result.message ?: "Unable to delete expenses"
                                )
                            )
                        }
                    }
                }
            }

            is ExpensesSettingsEvent.DeletePastExpenses -> {
                viewModelScope.launch {
                    when (val result = expensesRepository.deletePastExpenses(deleteAll = false)) {
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.Success("Expenses has been deleted successfully"))
                        }

                        is Resource.Error -> {
                            _eventFlow.emit(
                                UiEvent.Error(
                                    result.message ?: "Unable to delete expenses"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getAllExpenses() {
        viewModelScope.launch {
            expensesRepository.getAllExpenses("","").collectLatest { result ->
                _state.value = result
            }
        }
    }
}