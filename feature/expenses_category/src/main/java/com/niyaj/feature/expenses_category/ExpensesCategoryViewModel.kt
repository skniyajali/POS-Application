package com.niyaj.feature.expenses_category

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.ExpensesCategoryRepository
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

@HiltViewModel
class ExpensesCategoryViewModel @Inject constructor(
    private val repository: ExpensesCategoryRepository
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    val expensesCategories = snapshotFlow { mSearchText.value }.flatMapLatest { searchText ->
        repository.getAllExpensesCategory(searchText).mapLatest { items ->
            totalItems = items.map { it.expensesCategoryId }

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
            val result = repository.deleteExpensesCategories(selectedItems.toList())

            when (result) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error("Unable to delete categories"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success("${mSelectedItems.size} categories has been deleted")
                    )
                }
            }
        }

        mSelectedItems.clear()
    }

}