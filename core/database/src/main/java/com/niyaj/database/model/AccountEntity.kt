package com.niyaj.database.model

import com.niyaj.model.Account
import com.niyaj.model.RESTAURANT_ID
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AccountEntity() : RealmObject {
    @PrimaryKey
    var restaurantId: String = ""

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
        createdAt: String,
        updatedAt: String? = null,
    ) : this() {
        this.restaurantId = restaurantId
        this.email = email
        this.phone = phone
        this.password = password
        this.isLoggedIn = isLoggedIn
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun AccountEntity.toExternalModel(): Account {
    return Account(
        restaurantId = this.restaurantId,
        email = this.email,
        phone = this.phone,
        password = this.password,
        isLoggedIn = this.isLoggedIn,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}