package com.niyaj.data.mapper

import com.niyaj.database.model.ProductEntity
import com.niyaj.model.Product
import org.mongodb.kbson.BsonObjectId

fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        productId = productId.ifEmpty { BsonObjectId().toHexString() },
        category = category?.toEntity(),
        productName = productName,
        productPrice = productPrice,
        productAvailability = productAvailability,
        createdAt = createdAt.ifEmpty { System.currentTimeMillis().toString() },
        updatedAt = updatedAt
    )
}