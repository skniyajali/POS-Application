package com.niyaj.data.mapper

import com.niyaj.database.model.CategoryEntity
import com.niyaj.model.Category

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = categoryId,
        categoryName = categoryName,
        categoryAvailability = categoryAvailability,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}