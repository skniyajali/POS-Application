package com.niyaj.data.mapper

import com.niyaj.database.model.RestaurantInfoEntity
import com.niyaj.model.RestaurantInfo

fun RestaurantInfo.toEntity(): RestaurantInfoEntity {
    return RestaurantInfoEntity(
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