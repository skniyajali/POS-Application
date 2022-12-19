package com.niyaj.popos.realm.customer.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Customer: RealmObject {

    @PrimaryKey
    var customerId: String = ""

    var customerPhone: String = ""

    var customerName: String? = null

    var customerEmail: String? = null

    var created_at: String = ""

    var updated_at: String? = null
}