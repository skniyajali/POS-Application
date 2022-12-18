package com.niyaj.popos.domain.use_cases.cart_order

import com.niyaj.popos.domain.model.CartOrder
import com.niyaj.popos.domain.repository.CartOrderRepository
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAllCartOrders(
    private val cartOrderRepository: CartOrderRepository
) {
    suspend operator fun invoke(
        searchText: String = ""
    ): Flow<Resource<List<CartOrder>>> = flow {
         cartOrderRepository.getAllCartOrders().collect { result ->
             when (result){
                 is Resource.Loading -> {
                     emit(Resource.Loading(result.isLoading))
                 }
                 is Resource.Success -> {
                     emit(Resource.Success(
                         result.data?.filter { cartOrder ->
                             if (searchText.isNotEmpty()){
                                 cartOrder.cartOrderStatus.contains(searchText, true) ||
                                 cartOrder.cartOrderId.contains(searchText, true) ||
                                 cartOrder.customer?.customerPhone?.contains(searchText, true) == true ||
                                 cartOrder.customer?.customerName?.contains(searchText, true) == true ||
                                 cartOrder.address?.addressName?.contains(searchText, true) == true ||
                                 cartOrder.address?.shortName?.contains(searchText, true) == true ||
                                 cartOrder.cartOrderType.contains(searchText, true) ||
                                 cartOrder.orderId.contains(searchText, true) ||
                                 cartOrder.created_at?.contains(searchText, true) == true ||
                                 cartOrder.updated_at?.contains(searchText, true) == true

                             }else {
                                 true
                             }
                         }
                     ))
                 }
                 is Resource.Error -> {
                     emit(Resource.Error(result.message ?: "Unable to get data from database"))
                 }
             }
        }
    }
}