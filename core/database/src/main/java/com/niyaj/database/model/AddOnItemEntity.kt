package com.niyaj.database.model

import com.niyaj.model.AddOnItem
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AddOnItemEntity() : RealmObject {
    @PrimaryKey
    var addOnItemId: String = ""

    var itemName: String = ""

    var itemPrice: Int = 0

    var isApplicable: Boolean = true

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        addOnItemId: String = "",
        itemName: String = "",
        itemPrice: Int = 0,
        isApplicable: Boolean = true,
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.addOnItemId = addOnItemId
        this.itemName = itemName
        this.itemPrice = itemPrice
        this.isApplicable = isApplicable
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}


fun AddOnItemEntity.toExternalModel(): AddOnItem {
    return AddOnItem(
        addOnItemId = addOnItemId,
        itemName = itemName,
        itemPrice = itemPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}