package com.niyaj.model

import com.niyaj.common.utils.getAllCapitalizedLetters
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class Product(
    val productId : String = "",

    val category : Category? = null,

    val productName : String = "",

    val productPrice : Int = 0,

    val productAvailability : Boolean = true,

    val createdAt : String = System.currentTimeMillis().toString(),

    val updatedAt : String? = null,
)


/**
 *
 */
fun List<Product>.filterProducts(searchText : String) : List<Product> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.productName.contains(searchText, true) ||
                    it.productPrice.toString().contains(searchText, true) ||
                    it.productAvailability.toString().contains(searchText, true) ||
                    getAllCapitalizedLetters(it.productName).contains(searchText, true)
        }
    } else this

}

/**
 *
 */
fun List<Product>.filterByCategory(categoryId : String) : List<Product> {
    return if (categoryId.isNotEmpty()) {
        this.filter {
            it.category?.categoryId == categoryId
        }
    }else this
}