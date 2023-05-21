package com.niyaj.popos.features.charges.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Charges() : RealmObject {

    @PrimaryKey
    var chargesId: String = ""

    var chargesName: String = ""

    var chargesPrice: Int = 0

    var isApplicable: Boolean = false

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        chargesId: String = "",
        chargesName: String = "",
        chargesPrice: Int = 0,
        isApplicable: Boolean = false,
        createdAt: String = "",
        updatedAt: String? = null
    ) : this() {
        this.chargesId = chargesId
        this.chargesName = chargesName
        this.chargesPrice = chargesPrice
        this.isApplicable = isApplicable
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun Charges.filterCharges(searchText: String): Boolean {
    return this.chargesName.contains(searchText, true) ||
            this.chargesPrice.toString().contains(searchText, true) ||
            this.createdAt.contains(searchText, true) ||
            this.updatedAt?.contains(searchText, true) == true
}