package com.niyaj.feature.category

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CategoryRepository
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
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
) : BaseViewModel() {

    override var totalItems: List<String> = emptyList()

    val categories = snapshotFlow { mSearchText.value }.flatMapLatest {
        repository.getAllCategories(it)
    }.mapLatest { list ->
        totalItems = list.map { it.categoryId }
        if (list.isEmpty()) UiState.Empty else UiState.Success(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading,
    )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = repository.deleteCategories(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success(
                            "${selectedItems.size} item deleted successfully"
                        )
                    )

                    mSelectedItems.clear()
                }

                is Resource.Error -> {
                    mEventFlow.emit(
                        UiEvent.Error(result.message ?: "Unable to delete items")
                    )
                }
            }
        }
    }
}