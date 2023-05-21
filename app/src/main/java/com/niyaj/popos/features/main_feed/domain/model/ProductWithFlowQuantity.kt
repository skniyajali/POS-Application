package com.niyaj.popos.features.main_feed.domain.model

import com.niyaj.popos.utils.getAllCapitalizedLetters
import kotlinx.coroutines.flow.Flow

data class ProductWithFlowQuantity(
    val categoryId: String,
    val productId: String,
    val productName: String,
    val productPrice: Int,
    val quantity: Flow<Int>
)

fun ProductWithFlowQuantity.filterByCategory(categoryId : String): Boolean {
    return if (categoryId.isNotEmpty()) {
        this.categoryId == categoryId
    }else true
}

fun ProductWithFlowQuantity.filterBySearch(searchText: String): Boolean {
    return if (searchText.isNotEmpty()) {
        this.productName.contains(searchText, true) ||
                this.productPrice.toString().contains(searchText, true) ||
                getAllCapitalizedLetters(this.productName).contains(searchText, true)
    }else true
}