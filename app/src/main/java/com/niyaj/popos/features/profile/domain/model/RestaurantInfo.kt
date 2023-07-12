package com.niyaj.popos.features.profile.domain.model

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.niyaj.popos.features.common.util.ImageStorageManager
import com.niyaj.popos.utils.Constants.PAYMENT_QR_DATA
import com.niyaj.popos.utils.Constants.PRINT_LOGO
import com.niyaj.popos.utils.Constants.RESTAURANT_ADDRESS
import com.niyaj.popos.utils.Constants.RESTAURANT_DESCRIPTION
import com.niyaj.popos.utils.Constants.RESTAURANT_EMAIL
import com.niyaj.popos.utils.Constants.RESTAURANT_ID
import com.niyaj.popos.utils.Constants.RESTAURANT_LOGO
import com.niyaj.popos.utils.Constants.RESTAURANT_NAME
import com.niyaj.popos.utils.Constants.RESTAURANT_PRIMARY_PHONE
import com.niyaj.popos.utils.Constants.RESTAURANT_SECONDARY_PHONE
import com.niyaj.popos.utils.Constants.RESTAURANT_TAGLINE
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class RestaurantInfo(): RealmObject {

    @PrimaryKey
    var restaurantId = RESTAURANT_ID

    var name: String = RESTAURANT_NAME

    var email: String = RESTAURANT_EMAIL

    var primaryPhone: String = RESTAURANT_PRIMARY_PHONE

    var secondaryPhone: String = RESTAURANT_SECONDARY_PHONE

    var tagline: String = RESTAURANT_TAGLINE

    var description: String = RESTAURANT_DESCRIPTION

    var address: String = RESTAURANT_ADDRESS

    var logo: String = ""

    var printLogo: String = ""

    var paymentQrCode: String = PAYMENT_QR_DATA

    var createdAt: String = System.currentTimeMillis().toString()

    var updatedAt: String? = null

    constructor(
        name: String = RESTAURANT_NAME,
        tagline: String = RESTAURANT_TAGLINE,
        email: String = RESTAURANT_EMAIL,
        primaryPhone: String = RESTAURANT_PRIMARY_PHONE,
        secondaryPhone: String = RESTAURANT_SECONDARY_PHONE,
        description: String = RESTAURANT_DESCRIPTION,
        address: String = RESTAURANT_ADDRESS,
        paymentQrCode: String = PAYMENT_QR_DATA,
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