package com.niyaj.feature.product.details

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.common.utils.toBarDate
import com.niyaj.data.repository.ProductRepository
import com.niyaj.model.OrderType
import com.niyaj.model.Product
import com.niyaj.ui.event.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val productId = savedStateHandle.get<String>("productId") ?: ""

    private val _totalOrders = MutableStateFlow(ProductTotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    val product = snapshotFlow { productId }.mapLatest {
        productRepository.getProductById(productId).data ?: Product()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Product()
    )

    val orderDetails = snapshotFlow { productId }.flatMapLatest {
        productRepository.getProductOrders(it)
    }
        .mapLatest { orders ->
            val productPrice = product.value.productPrice

            val groupByDate = orders.groupBy { it.orderedDate.toBarDate }
            val grpByOrderType = orders.groupBy { it.orderType }

            val dineInOrders =
                grpByOrderType.getOrElse(OrderType.DineIn, defaultValue = { emptyList() })
            val dineOutOrders =
                grpByOrderType.getOrElse(OrderType.DineOut, defaultValue = { emptyList() })

            val dineInAmount = dineInOrders.sumOf { it.quantity }.times(productPrice)
            val dineOutAmount = dineOutOrders.sumOf { it.quantity }.times(productPrice)

            val totalAmount = dineInAmount + dineOutAmount

            val startDate = if (orders.isNotEmpty()) orders.first().orderedDate else ""
            val endDate = if (orders.isNotEmpty()) orders.last().orderedDate else ""

            val mostOrderItemDate =
                if (groupByDate.isNotEmpty()) groupByDate.maxBy { it.value.size }.key else ""
            val mostOrderQtyDate =
                if (groupByDate.isNotEmpty()) groupByDate.maxBy { entry -> entry.value.sumOf { it.quantity } }.key else ""


            _totalOrders.value = _totalOrders.value.copy(
                totalAmount = totalAmount.toString(),
                dineInAmount = dineInAmount.toString(),
                dineInQty = dineInOrders.size,
                dineOutAmount = dineOutAmount.toString(),
                dineOutQty = dineOutOrders.size,
                mostOrderItemDate = mostOrderItemDate,
                mostOrderQtyDate = mostOrderQtyDate,
                datePeriod = Pair(startDate, endDate)
            )

            if (orders.isEmpty()) UiState.Empty else UiState.Success(orders)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Empty
        )

}