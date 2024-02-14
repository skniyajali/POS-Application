package com.niyaj.database.model

import com.niyaj.model.RESTAURANT_ID
import com.niyaj.model.RestaurantInfo
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RestaurantInfoEntity(): RealmObject {

    @PrimaryKey
    var restaurantId = RESTAURANT_ID

    var name: String = ""

    var email: String = ""

    var primaryPhone: String = ""

    var secondaryPhone: String = ""

    var tagline: String = ""

    var description: String = ""

    var address: String = ""

    var logo: String = ""

    var printLogo: String = ""

    var paymentQrCode: String = ""

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

    constructor(
        restaurantId: String,
        name: String,
        tagline: String,
        email: String,
        primaryPhone: String,
        secondaryPhone: String,
        description: String,
        address: String,
        paymentQrCode: String,
        logo: String = "",
        printLogo: String = "",
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null
    ): this() {
        this.restaurantId = restaurantId
        this.name = name
        this.tagline = tagline
        this.email = email
        this.primaryPhone = primaryPhone
        this.secondaryPhone = secondaryPhone
        this.description = description
        this.address = address
        this.paymentQrCode = paymentQrCode
        this.printLogo = printLogo
        this.logo = logo
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun RestaurantInfoEntity.toExternalModel(): RestaurantInfo {
    return RestaurantInfo(
        restaurantId = restaurantId,
        name = name,
        email = email,
        primaryPhone = primaryPhone,
        secondaryPhone = secondaryPhone,
        tagline = tagline,
        description = description,
        address = address,
        logo = logo,
        printLogo = printLogo,
        paymentQrCode = paymentQrCode,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}