package com.niyaj.popos.realm.charges.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Charges: RealmObject {

    @PrimaryKey
    var chargesId: String = ""

    var chargesName: String = ""

    var chargesPrice: Int = 0

    var isApplicable: Boolean = false

    var createdAt: String = ""

    var updatedAt: String? = null
}