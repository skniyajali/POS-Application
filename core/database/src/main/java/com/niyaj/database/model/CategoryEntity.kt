package com.niyaj.database.model

import com.niyaj.model.Category
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CategoryEntity(): RealmObject {
    @PrimaryKey
    var categoryId: String = ""

    var categoryName: String = ""

    var categoryAvailability: Boolean = true

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        categoryId: String = "",
        categoryName: String = "",
        categoryAvailability: Boolean = true,
        createdAt: String = "",
        updatedAt: String? = null
    ): this(){
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.categoryAvailability = categoryAvailability
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun CategoryEntity.toExternalModel(): Category {
    return Category(
        categoryId = categoryId,
        categoryName = categoryName,
        categoryAvailability = categoryAvailability,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}