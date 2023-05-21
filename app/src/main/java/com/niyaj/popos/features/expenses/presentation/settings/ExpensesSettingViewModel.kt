package com.niyaj.popos.features.expenses.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.use_cases.GetAllExpenses
import com.niyaj.popos.features.expenses.presentation.ExpensesState
import com.niyaj.popos.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExpensesSettingViewModel @Inject constructor(
    private val expensesRepository : ExpensesRepository,
    private val getAllExpenses : GetAllExpenses
): ViewModel() {

    private val _state = MutableStateFlow(ExpensesState())
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
        when(event) {

            is ExpensesSettingsEvent.SelectExpenses -> {
                viewModelScope.launch {
                    if(_selectedExpenses.contains(event.expensesId)){
                        _selectedExpenses.remove(event.expensesId)
                    }else{
                        _selectedExpenses.add(event.expensesId)
                    }
                }
            }

            is ExpensesSettingsEvent.SelectAllExpenses -> {
                val selectCount = when(event.type) {
                    Constants.ImportExportType.IMPORT -> count++
                    Constants.ImportExportType.EXPORT -> exportCount++
                }

                val expenses = when(event.type) {
                    Constants.ImportExportType.IMPORT -> _importExportedExpenses.value
                    Constants.ImportExportType.EXPORT -> _state.value.expenses
                }

                if (expenses.isNotEmpty()){
                    expenses.forEach { expense ->
                        if (selectCount % 2 != 0){

                            val selectedExpense = _selectedExpenses.find { it == expense.expensesId }

                            if (selectedExpense == null){
                                _selectedExpenses.add(expense.expensesId)
                            }
                        }else {
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
                    val data = _importExportedExpenses.value.find { expense -> expense.expensesId == it }
                    if (data != null) expenses.add(data)
                }

                viewModelScope.launch {
                    when (val result = expensesRepository.importExpenses(expenses.toList())){
                        is Resource.Loading -> { }
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("${expenses.toList().size} expenses imported successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to import expenses"))
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
                    if (_selectedExpenses.isEmpty()){
                        _importExportedExpenses.emit(_state.value.expenses)
                    } else {
                        val expenses = mutableListOf<Expenses>()

                        _selectedExpenses.forEach { id ->
                            val customer = _state.value.expenses.find { it.expensesId == id }
                            if (customer != null){
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
                    when(val result = expensesRepository.deletePastExpenses(deleteAll = true)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("All Expenses has been deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete expenses"))
                        }
                    }
                }
            }

            is ExpensesSettingsEvent.DeletePastExpenses -> {
                viewModelScope.launch {
                    when(val result = expensesRepository.deletePastExpenses(deleteAll = false)){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            _eventFlow.emit(UiEvent.OnSuccess("Expenses has been deleted successfully"))
                        }
                        is Resource.Error -> {
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to delete expenses"))
                        }
                    }
                }
            }
        }
    }

    private fun getAllExpenses() {
        viewModelScope.launch {
            getAllExpenses.invoke().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        withContext(Dispatchers.Main) {
                            _state.value = _state.value.copy(isLoading = result.isLoading)
                        }
                    }
                    is Resource.Success -> {
                        result.data?.let { expenses ->
                            _state.value = _state.value.copy(expenses = expenses)
                        }
                    }
                    is Resource.Error -> {
                        withContext(Dispatchers.Main){
                            _state.value = _state.value.copy(error = "Unable to load resources")
                            _eventFlow.emit(UiEvent.OnError(result.message ?: "Unable to load resources"))
                        }
                    }
                }
            }
        }
    }
}