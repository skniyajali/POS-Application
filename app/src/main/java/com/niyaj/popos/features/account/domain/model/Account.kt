package com.niyaj.popos.features.account.domain.model

import com.niyaj.popos.utils.Constants.RESTAURANT_ID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Account(): RealmObject {
    @PrimaryKey
    var restaurantId: String = RESTAURANT_ID

    var email: String = ""

    var phone: String = ""

    var password: String = ""

    var isLoggedIn: Boolean = true

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

    constructor(
        restaurantId: String = RESTAURANT_ID,
        email: String,
        phone: String,
        password: String,
        isLoggedIn: Boolean,
    ): this() {
        this.restaurantId = restaurantId
        this.email = email
        this.phone = phone
        this.password = password
        this.isLoggedIn = isLoggedIn
    }
}