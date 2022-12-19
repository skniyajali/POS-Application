package com.niyaj.popos.realm.product

import com.niyaj.popos.realm.category.domain.model.Category
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ProductRealm(): RealmObject {

    @PrimaryKey
    var _id: String = ""

    var category: Category? = null

    var productName: String = ""

    var productPrice: Int = 0

    var productAvailability: Boolean? = true

    var created_at: String? = System.currentTimeMillis().toString()

    var updated_at: String? = null
}