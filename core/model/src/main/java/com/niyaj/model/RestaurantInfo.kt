package com.niyaj.model

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.niyaj.common.utils.ImageStorageManager
import com.niyaj.core.model.R

data class RestaurantInfo(
    val restaurantId: String = RESTAURANT_ID,

    val name: String = RESTAURANT_NAME,

    val email: String = RESTAURANT_EMAIL,

    val primaryPhone: String = RESTAURANT_PRIMARY_PHONE,

    val secondaryPhone: String = RESTAURANT_SECONDARY_PHONE,

    val tagline: String = RESTAURANT_TAGLINE,

    val description: String = RESTAURANT_DESCRIPTION,

    val address: String = RESTAURANT_ADDRESS,

    val logo: String = "",

    val printLogo: String = "",

    val paymentQrCode: String = RESTAURANT_PAYMENT_QR_DATA,

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
) {
    fun getRestaurantLogo(context: Context): Bitmap? {
        return if (logo.isEmpty()) {
            context.getDrawable(RESTAURANT_LOGO.toInt())?.toBitmap()!!
        } else {
            ImageStorageManager.getImageFromInternalStorage(context, logo)
        }
    }

    fun getRestaurantPrintLogo(context: Context): Bitmap? {
        return if (printLogo.isEmpty()) {
            context.getDrawable(PRINT_LOGO.toInt())?.toBitmap()
        } else {
            ImageStorageManager.getImageFromInternalStorage(context, printLogo)
        }
    }
}


const val RESTAURANT_ID = "22222222"

const val RESTAURANT_NAME = "Popos Highlight"
const val RESTAURANT_TAGLINE = "- Pure And Tasty -"
const val RESTAURANT_DESCRIPTION = "Multi Cuisine Veg & Non-Veg Restaurant"
const val RESTAURANT_EMAIL = "poposhighlight@gmail.com"
const val RESTAURANT_SECONDARY_PHONE: String = "9597185001"
const val RESTAURANT_PRIMARY_PHONE: String = "9500825077"
const val RESTAURANT_ADDRESS =
    "Chinna Seeragapadi, Salem, TamilNadu, India 636308, Opp. of VIMS Hospital"

const val RESTAURANT_PAYMENT_QR_DATA =
    "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

const val RESTAURANT_LOGO_NAME = "reslogo"
const val RESTAURANT_PRINT_LOGO_NAME = "printlogo"

val RESTAURANT_LOGO = R.drawable.logo_new.toString()
val PRINT_LOGO = R.drawable.reslogo.toString()