package com.niyaj.popos.realm.addon_item.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AddOnItem : RealmObject {
    @PrimaryKey
    var addOnItemId: String = ""

    var itemName: String = ""

    var itemPrice: Int = 0

    var createdAt: String = ""

    var updatedAt: String? = null
}