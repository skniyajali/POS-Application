package com.niyaj.popos.domain.use_cases.common

import com.niyaj.popos.domain.repository.CommonRepository

class GetTotalPriceOfOrder(
    private val commonRepository: CommonRepository
) {

    operator fun invoke(cartOrderId: String): Pair<Int, Int>{
        return commonRepository.countTotalPrice(cartOrderId)
    }
}