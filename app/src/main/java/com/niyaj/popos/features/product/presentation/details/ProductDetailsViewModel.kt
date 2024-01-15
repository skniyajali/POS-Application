package com.niyaj.popos.features.product.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.niyaj.popos.common.utils.toBarDate
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val productRepository : ProductRepository,
    savedStateHandle : SavedStateHandle,
): ViewModel() {

    private val _product = MutableStateFlow(Product())
    val product = _product.asStateFlow()

    private val _orderDetails = MutableStateFlow(ProductOrderState())
    val orderDetails = _orderDetails.asStateFlow()

    private val _totalOrders = MutableStateFlow(ProductTotalOrderDetails())
    val totalOrders = _totalOrders.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<String>("productId")?.let { productId ->
            getProductDetails(productId)
            getOrderDetails(productId)
        }
    }

    private fun getOrderDetails(productId: String) {
        viewModelScope.launch {
            productRepository.getProductOrders(productId).collectLatest {result ->
                when (result) {
                    is Resource.Loading -> {
                        _orderDetails.value = _orderDetails.value.copy(isLoading = result.isLoading)
                    }
                    is Resource.Success -> {
                        result.data?.let { orders ->
                            _orderDetails.value = _orderDetails.value.copy(isLoading = true)

                            val productPrice = _product.value.productPrice

                            val groupByDate = orders.groupBy { it.orderedDate.toBarDate }
                            val grpByOrderType = orders.groupBy { it.orderType }

                            val dineInOrders = grpByOrderType.getOrElse(CartOrderType.DineIn.orderType, defaultValue = { emptyList() })
                            val dineOutOrders = grpByOrderType.getOrElse(CartOrderType.DineOut.orderType, defaultValue = { emptyList() })

                            val dineInAmount = dineInOrders.sumOf { it.quantity }.times(productPrice)
                            val dineOutAmount = dineOutOrders.sumOf { it.quantity}.times(productPrice)

                            val totalAmount = dineInAmount + dineOutAmount

                            val startDate = if (orders.isNotEmpty()) orders.first().orderedDate else ""
                            val endDate = if (orders.isNotEmpty()) orders.last().orderedDate else ""

                            val mostOrderItemDate = if (groupByDate.isNotEmpty()) groupByDate.maxBy { it.value.size }.key else ""
                            val mostOrderQtyDate = if (groupByDate.isNotEmpty()) groupByDate.maxBy { entry -> entry.value.sumOf { it.quantity } }.key else ""


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

                            _orderDetails.value = _orderDetails.value.copy(
                                productOrders = orders,
                                productPrice = productPrice,
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _orderDetails.value = _orderDetails.value.copy(hasError = result.message)
                    }
                }
            }
        }
    }

    private fun getProductDetails(productId: String) {
        viewModelScope.launch {
            when(val result = productRepository.getProductById(productId)) {
                is Resource.Loading -> {}
                is Resource.Success -> {
                    result.data?.let {
                        _product.value = it
                    }
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.Error(result.message ?: "Unable to get product details"))
                }
            }
        }
    }
}