package com.niyaj.popos.features.main_feed.domain.use_cases

import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository

class GetProductQuantity(private val mainFeedRepository: MainFeedRepository) {

    suspend operator fun invoke(cartOrderId: String, productId: String): Int {
        return mainFeedRepository.getProductQuantity(cartOrderId, productId)
    }
}