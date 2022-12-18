package com.niyaj.popos.domain.use_cases.main_feed

data class MainFeedUseCases(
    val getMainFeedSelectedOrder: GetMainFeedSelectedOrder,
    val getMainFeedCategories: GetMainFeedCategories,
    val getMainFeedProducts: GetMainFeedProducts,
    val getProductsPager: GetProductsPager
)
