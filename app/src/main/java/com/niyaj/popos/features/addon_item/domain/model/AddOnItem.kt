package com.niyaj.popos.features.addon_item.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AddOnItem() : RealmObject {
    @PrimaryKey
    var addOnItemId: String = ""

    var itemName: String = ""

    var itemPrice: Int = 0

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        addOnItemId: String = "",
        itemName: String = "",
        itemPrice: Int = 0,
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.addOnItemId = addOnItemId
        this.itemName = itemName
        this.itemPrice = itemPrice
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}