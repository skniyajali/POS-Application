package com.niyaj.data.mapper

import com.niyaj.database.model.CategoryEntity
import com.niyaj.model.Category
import org.mongodb.kbson.BsonObjectId

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = categoryId.ifEmpty { BsonObjectId().toHexString() },
        categoryName = categoryName,
        categoryAvailability = categoryAvailability,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}