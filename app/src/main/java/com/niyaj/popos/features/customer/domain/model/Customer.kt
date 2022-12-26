package com.niyaj.popos.features.customer.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Customer(): RealmObject {

    @PrimaryKey
    var customerId: String = ""

    var customerPhone: String = ""

    var customerName: String? = null

    var customerEmail: String? = null

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        customerId: String = "",
        customerPhone: String = "",
        customerName: String = "",
        customerEmail: String = "",
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ): this() {
        this.customerId = customerId
        this.customerPhone = customerPhone
        this.customerName = customerName
        this.customerEmail = customerEmail
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}