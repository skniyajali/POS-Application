package com.niyaj.popos.realm.category.domain.model

import com.squareup.moshi.JsonClass
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

@JsonClass(generateAdapter = true)
class Category: RealmObject {
    @PrimaryKey
    var categoryId: String = ""

    var categoryName: String = ""

    var categoryAvailability: Boolean = true

    var createdAt: String = ""

    var updatedAt: String? = null
}