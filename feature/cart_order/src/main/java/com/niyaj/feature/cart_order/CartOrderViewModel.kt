package com.niyaj.feature.cart_order

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.domain.use_cases.GetAllCartOrders
import com.niyaj.ui.event.BaseViewModel
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartOrderViewModel @Inject constructor(
    private val getAllCartOrders: GetAllCartOrders,
    private val cartOrderRepository: CartOrderRepository
) : BaseViewModel() {
    override var totalItems: List<String> = emptyList()

    private val _viewAll = mutableStateOf(false)

    private val _selectedCartOrder = MutableStateFlow<String?>(null)
    val selectedCartOrder = _selectedCartOrder.asStateFlow()

    init {
        getSelectedOrder()
    }

    val cartOrders = snapshotFlow { mSearchText.value }
        .combine(snapshotFlow { _viewAll.value }) { searchText, viewAll ->
            getAllCartOrders(searchText, viewAll)
        }.flatMapLatest { listFlow ->
            listFlow.mapLatest { list ->
                val data = list.sortedByDescending { it.cartOrderId == _selectedCartOrder.value }
                totalItems = list.map { it.cartOrderId }
                list.groupBy { (it.updatedAt ?: it.createdAt).toPrettyDate() }
            }
        }.mapLatest { data ->
            if (data.isEmpty()) UiState.Empty else UiState.Success(data)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun onClickViewAllOrder() {
        viewModelScope.launch {
            _viewAll.value = !_viewAll.value
        }
    }

    override fun deleteItems() {
        super.deleteItems()

        viewModelScope.launch {
            when (cartOrderRepository.deleteCartOrders(mSelectedItems.toList())) {
                is Resource.Success -> {
                    getSelectedOrder()
                    mEventFlow.emit(UiEvent.Success("CartOrder Deleted Successfully"))
                }

                is Resource.Error -> {
                    mEventFlow.emit(UiEvent.Error("Unable to delete CartOrder"))
                }
            }

            mSelectedItems.clear()
        }
    }

    fun selectCartOrder() {
        viewModelScope.launch {
            cartOrderRepository.addSelectedCartOrder(mSelectedItems.first())
            mSelectedItems.clear()
        }
    }

    private fun getSelectedOrder() {
        viewModelScope.launch {
            cartOrderRepository.getSelectedCartOrders().collectLatest { result ->
                result.let {
                    _selectedCartOrder.value = it
                }
            }
        }
    }

}