package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllCartOrders(
    private val cartOrderRepository: CartOrderRepository
) {
    suspend operator fun invoke(
        searchText: String = "",
        viewAll: Boolean = false,
    ): Flow<Resource<List<CartOrder>>> = channelFlow {
        cartOrderRepository.getAllCartOrders(viewAll).collectLatest { result ->
            when (result){
                is Resource.Loading -> {
                    send(Resource.Loading(result.isLoading))
                }
                is Resource.Success -> {
                    val data = result.data?.filter { cartOrder ->
                        if (searchText.isNotEmpty()){
                            cartOrder.cartOrderStatus.contains(searchText, true) ||
                                    cartOrder.cartOrderId.contains(searchText, true) ||
                                    cartOrder.customer?.customerPhone?.contains(searchText, true) == true ||
                                    cartOrder.customer?.customerName?.contains(searchText, true) == true ||
                                    cartOrder.address?.addressName?.contains(searchText, true) == true ||
                                    cartOrder.address?.shortName?.contains(searchText, true) == true ||
                                    cartOrder.orderType.contains(searchText, true) ||
                                    cartOrder.orderId.contains(searchText, true) ||
                                    cartOrder.createdAt.contains(searchText, true) ||
                                    cartOrder.updatedAt?.contains(searchText, true) == true

                        }else {
                            true
                        }
                    }

                    send(Resource.Success(data))
                }
                is Resource.Error -> {
                    send(Resource.Error(result.message ?: "Unable to get data from database"))
                }
            }
        }
    }
}