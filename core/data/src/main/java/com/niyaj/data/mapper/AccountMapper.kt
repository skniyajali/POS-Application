package com.niyaj.data.mapper

import com.niyaj.database.model.AccountEntity
import com.niyaj.model.Account

fun Account.toEntity(): AccountEntity {
    return AccountEntity(
        restaurantId = this.restaurantId,
        email = this.email,
        phone = this.phone,
        password = this.password,
        isLoggedIn = this.isLoggedIn,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}