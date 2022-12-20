package com.niyaj.popos.features.category.domain.model

import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@JsonClass(generateAdapter = true)
class Category(): RealmObject {
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