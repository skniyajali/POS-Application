package com.niyaj.popos.features.main_feed.domain.use_cases

data class MainFeedUseCases(
    val getMainFeedSelectedOrder: GetMainFeedSelectedOrder,
    val getMainFeedCategories: GetMainFeedCategories,
    val getMainFeedProducts: GetMainFeedProducts,
    val getProductsPager: GetProductsPager,
    val getProductQuantity: GetProductQuantity
)
