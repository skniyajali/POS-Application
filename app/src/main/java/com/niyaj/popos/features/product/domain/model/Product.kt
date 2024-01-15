package com.niyaj.popos.features.product.domain.model

import com.niyaj.popos.common.utils.getAllCapitalizedLetters
import com.niyaj.popos.features.category.domain.model.Category
import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 *
 */
@JsonClass(generateAdapter = true)
class Product() : RealmObject {
    @PrimaryKey
    var productId : String = ""

    var category : Category? = null

    var productName : String = ""

    var productPrice : Int = 0

    var productAvailability : Boolean = true

    var createdAt : String = ""

    var updatedAt : String? = null

    constructor(
        productId : String = "",
        category : Category? = null,
        productName : String = "",
        productPrice : Int = 0,
        productAvailability : Boolean = true,
        createdAt : String = "",
        updatedAt : String? = null
    ) : this() {
        this.productId = productId
        this.category = category
        this.productName = productName
        this.productPrice = productPrice
        this.productAvailability = productAvailability
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

/**
 *
 */
fun Product.filterProducts(searchText : String) : Boolean {
    return if (searchText.isNotEmpty()) {
        this.productName.contains(searchText, true) ||
                this.productPrice.toString().contains(searchText, true) ||
                this.productAvailability.toString().contains(searchText, true) ||
                getAllCapitalizedLetters(this.productName).contains(searchText, true)
    } else true

}

/**
 *
 */
fun Product.filterByCategory(categoryId : String) : Boolean {
    return if (categoryId.isNotEmpty()) {
        this.category?.categoryId == categoryId
    }else true
}