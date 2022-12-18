package com.niyaj.popos.domain.use_cases.order

import com.niyaj.popos.domain.model.Cart
import com.niyaj.popos.domain.repository.OrderRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.SortType
import com.niyaj.popos.domain.util.filter_items.FilterOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllOrders(
    private val orderRepository: OrderRepository
){
    operator fun invoke(
        filterOrder: FilterOrder,
        searchText: String = "",
        startDate: String,
        endDate: String,
    ): Flow<Resource<List<Cart>>> {
        return flow {
            orderRepository.getAllOrders(startDate, endDate).collect { result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { orders ->
                                when (filterOrder.sortType){
                                    is SortType.Ascending -> {
                                        when(filterOrder){
                                            is FilterOrder.ByCustomerAddress -> { orders.sortedBy { it.cartOrder?.address?.addressName } }
                                            is FilterOrder.ByCustomerName -> { orders.sortedBy { it.cartOrder?.customer?.customerPhone } }
                                            is FilterOrder.ByOrderPrice -> { orders.sortedBy { it.orderPrice.first } }
                                            is FilterOrder.ByOrderStatus -> { orders.sortedBy { it.cartOrder?.cartOrderStatus } }
                                            is FilterOrder.ByOrderType -> { orders.sortedBy { it.cartOrder?.cartOrderType } }
                                            is FilterOrder.ByUpdatedDate -> { orders.sortedBy { it.cartOrder?.updated_at } }
                                        }
                                    }
                                    is SortType.Descending -> {
                                        when(filterOrder){
                                            is FilterOrder.ByCustomerAddress -> { orders.sortedByDescending { it.cartOrder?.address?.addressName } }
                                            is FilterOrder.ByCustomerName -> { orders.sortedByDescending { it.cartOrder?.customer?.customerPhone } }
                                            is FilterOrder.ByOrderPrice -> { orders.sortedByDescending { it.orderPrice.first } }
                                            is FilterOrder.ByOrderStatus -> { orders.sortedByDescending { it.cartOrder?.cartOrderStatus } }
                                            is FilterOrder.ByOrderType -> { orders.sortedByDescending { it.cartOrder?.cartOrderType } }
                                            is FilterOrder.ByUpdatedDate -> { orders.sortedByDescending { it.cartOrder?.updated_at } }
                                        }
                                    }
                                }.filter { order ->
                                    order.cartOrder?.address?.addressName?.contains(searchText, true) == true ||
                                    order.cartOrder?.address?.shortName?.contains(searchText, true) == true ||
                                    order.cartOrder?.customer?.customerPhone?.contains(searchText, true) == true ||
                                    order.cartOrder?.customer?.customerName?.contains(searchText, true) == true ||
                                    order.cartOrder?.customer?.customerEmail?.contains(searchText, true) == true ||
                                    order.cartOrder?.cartOrderStatus?.contains(searchText, true) == true ||
                                    order.cartOrder?.cartOrderType?.contains(searchText, true) == true ||
                                    order.cartOrder?.created_at?.contains(searchText, true) == true ||
                                    order.orderPrice.first.toString().contains(searchText, true)
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get orders from repository"))
                    }
                }
            }
        }
    }
}
