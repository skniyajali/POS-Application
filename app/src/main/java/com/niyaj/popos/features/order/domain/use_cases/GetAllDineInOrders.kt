package com.niyaj.popos.features.order.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.domain.model.searchDineInOrder
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetAllDineInOrders(
    private val orderRepository : OrderRepository
) {
    suspend operator fun invoke(
        searchText : String = "",
        startDate : String,
        endDate : String
    ): Flow<Resource<List<DineInOrder>>> {
        return channelFlow {
            orderRepository.getDineInOrders(startDate, endDate).collectLatest { result ->
                when(result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.filter { it.searchDineInOrder(searchText) }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to find orders"))
                    }
                }
            }
        }
    }
}