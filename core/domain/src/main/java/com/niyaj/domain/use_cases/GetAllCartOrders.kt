package com.niyaj.domain.use_cases

import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.model.CartOrder
import com.niyaj.model.searchCartOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class GetAllCartOrders @Inject constructor(
    private val cartOrderRepository: CartOrderRepository
) {
    suspend operator fun invoke(
        searchText: String = "",
        viewAll: Boolean = false,
    ): Flow<List<CartOrder>> {
        return cartOrderRepository.getAllCartOrders(viewAll)
            .mapLatest { it.searchCartOrder(searchText) }
    }
}