package com.niyaj.feature.expenses

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.startOfDayTime
import com.niyaj.data.repository.ExpensesRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class ExpensesViewModel @Inject constructor(
    private val expensesRepository: ExpensesRepository
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    private val _totalAmount = MutableStateFlow("0")
    val totalAmount = _totalAmount.asStateFlow()

    private val _selectedDate = MutableStateFlow(startOfDayTime)
    val selectedDate = _selectedDate.asStateFlow()

    private val _text = snapshotFlow { searchText.value }

    @OptIn(ExperimentalCoroutinesApi::class)
    val expenses = _text.combine(_selectedDate) { text, date ->
        expensesRepository.getAllExpenses(text,date)
    }
        .flatMapLatest { it ->
            it.map { items ->
                totalItems = items.map { it.expensesId }
                _totalAmount.value = items.sumOf { it.expensesAmount.toInt() }.toString()

                if (items.isEmpty()) {
                    UiState.Empty
                } else UiState.Success(items)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun selectDate(selectedDate: String) {
        viewModelScope.launch {
            _selectedDate.value = selectedDate
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = expensesRepository.deleteExpenses(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error(result.message ?: "Unable to delete expenses"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(UiEvent.Success("${selectedItems.size} expenses has been deleted"))
                }
            }

            mSelectedItems.clear()
        }
    }
}