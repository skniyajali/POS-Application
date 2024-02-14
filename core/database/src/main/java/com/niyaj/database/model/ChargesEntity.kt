package com.niyaj.database.model

import com.niyaj.model.Charges
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ChargesEntity() : RealmObject {

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

fun ChargesEntity.toExternalModel(): Charges {
    return Charges(
        chargesId = chargesId,
        chargesName = chargesName,
        chargesPrice = chargesPrice,
        isApplicable = isApplicable,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}