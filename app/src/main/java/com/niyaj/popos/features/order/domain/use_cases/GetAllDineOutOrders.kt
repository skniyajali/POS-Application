package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.model.DineOutOrder
import com.niyaj.popos.features.order.domain.model.searchDineOutOrder
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllDineOutOrders(private val orderRepository : OrderRepository) {
    suspend operator fun invoke(
        searchText : String = "",
        startDate : String,
        endDate : String
    ): Flow<Resource<List<DineOutOrder>>> {
        return channelFlow {
            orderRepository.getDineOutOrders(startDate, endDate).collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.filter { it.searchDineOutOrder(searchText) }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get all orders"))
                    }
                }
            }
        }
    }
}