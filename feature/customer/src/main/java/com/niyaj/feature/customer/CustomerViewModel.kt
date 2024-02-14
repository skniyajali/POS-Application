package com.niyaj.feature.customer

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.data.repository.CustomerRepository
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

/**
 * Customer View Model
 * @author Sk Niyaj Ali
 *
 */
@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
) : BaseViewModel() {
    override var totalItems: List<String> = emptyList()

    @OptIn(ExperimentalCoroutinesApi::class)
    val customers = snapshotFlow { searchText.value }
        .flatMapLatest { it ->
            customerRepository.getAllCustomers(it)
                .onStart { UiState.Loading }
                .map { items ->
                    totalItems = items.map { it.customerId }
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
            when (customerRepository.deleteCustomers(selectedItems.toList())) {
                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error("Unable to delete customer"))
                }

                is Resource.Success -> {
                    mEventFlow.emit(
                        UiEvent.Success(
                            "${selectedItems.size} customer has been deleted"
                        )
                    )
                }
            }

            mSelectedItems.clear()
        }
    }
}
