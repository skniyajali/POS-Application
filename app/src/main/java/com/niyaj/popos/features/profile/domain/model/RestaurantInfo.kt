package com.niyaj.popos.features.profile.domain.model

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.niyaj.popos.common.utils.Constants.PRINT_LOGO
import com.niyaj.popos.common.utils.Constants.RESTAURANT_ID
import com.niyaj.popos.common.utils.Constants.RESTAURANT_LOGO
import com.niyaj.popos.features.common.util.ImageStorageManager
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RestaurantInfo(): RealmObject {

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

    fun getRestaurantLogo(context : Context): Bitmap? {
        return if (logo.isEmpty()) {
            context.getDrawable(RESTAURANT_LOGO.toInt())?.toBitmap()!!
        }else {
            ImageStorageManager.getImageFromInternalStorage(context, logo)
        }
    }

    fun getRestaurantPrintLogo(context : Context): Bitmap? {
        return if (printLogo.isEmpty()) {
            context.getDrawable(PRINT_LOGO.toInt())?.toBitmap()
        }else {
            ImageStorageManager.getImageFromInternalStorage(context, printLogo)
        }
    }
}