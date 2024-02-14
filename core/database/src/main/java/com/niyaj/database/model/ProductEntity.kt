package com.niyaj.database.model

import com.niyaj.model.Product
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 *
 */
class ProductEntity() : RealmObject {
    @PrimaryKey
    var productId : String = ""

    var category : CategoryEntity? = null

    var productName : String = ""

    var productPrice : Int = 0

    var productAvailability : Boolean = true

    var createdAt : String = ""

    var updatedAt : String? = null

    constructor(
        productId : String = "",
        category : CategoryEntity? = null,
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

fun ProductEntity.toExternalModel(): Product {
    return Product(
        productId = productId,
        category = category?.toExternalModel(),
        productName = productName,
        productPrice = productPrice,
        productAvailability = productAvailability,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}