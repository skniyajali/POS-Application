package com.niyaj.feature.addonitem

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddOnItemViewModel @Inject constructor(
    private val itemRepository: AddOnItemRepository
): BaseViewModel() {
    override var totalItems: List<String> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val addOnItems = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            itemRepository.getAllAddOnItems(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.addOnItemId }
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else UiState.Success(items)
                }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (val result = itemRepository.deleteAddOnItems(selectedItems.toList())) {
                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success(
                            "${selectedItems.size} item deleted successfully"
                        )
                    )
                }

                is Resource.Error -> {
                    mEventFlow.emit(
                        UiEvent.Error(result.message ?: "Unable to delete items")
                    )
                }
            }

            mSelectedItems.clear()
        }
    }
}