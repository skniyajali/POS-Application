package com.niyaj.popos.util

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object Constants {

    // github login token - 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

    const val SPLASH_SCREEN_DURATION = 100L

    const val PRINTER_DPI = 176

    const val PRINTER_WIDTH_MM = 58f

    const val PRINTER_NBR_LINE = 31

    const val PAYMENT_QR_DATA = "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

    const val REALM_DATABASE_NAME = "popos_highlight"

    const val REALM_PARTITION_NAME = "realm_partition"

    const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 30

    const val PRINT_ADDRESS_WISE_REPORT_LIMIT = 15

    const val PRINT_CUSTOMER_WISE_REPORT_LIMIT = 15

    const val IMPORT_TYPE = "Import"

    const val EXPORT_TYPE = "Export"

    const val JSON_FILE_NAME = "JSON"

    const val CSV_FILE_NAME = "CSV"

    const val JSON_FILE_TYPE = "application/json"

    const val CSV_FILE_TYPE = "text/*"

    const val JSON_FILE_EXTENSION = ".json"

    const val CSV_FILE_EXTENSION = ".csv"

    const val SAVEABLE_FILE_NAME = "popos"

    @RequiresApi(Build.VERSION_CODES.Q)
    val FILE_URI: Uri = MediaStore.Downloads.INTERNAL_CONTENT_URI

    const val PAID = "Paid"

    const val NOT_PAID = "Not Paid"

    const val DELETE_DATA_NOTIFICATION_CHANNEL_ID = "delete_data"
    const val DELETE_DATA_NOTIFICATION_CHANNEL_NAME = "Data Deletion"
    const val DELETE_DATA_INTERVAL_HOUR: Long = 15

    const val GENERATE_REPORT_CHANNEL_ID = "generate_report"
    const val GENERATE_REPORT_CHANNEL_NAME = "Generating Report"
    const val GENERATE_REPORT_INTERVAL_HOUR: Long = 1

    const val ADD_ON_EXCLUDE_ITEM_ONE = "Masala"

    const val ADD_ON_EXCLUDE_ITEM_TWO = "Mayonnaise"

    const val SELECTED_CART_ORDER_ID = "33333333"
    const val SETTINGS_ID = "11111111"
    const val RESTAURANT_ID = "22222222"

    const val RESTAURANT_NAME = "Popos Highlight"
    const val RESTAURANT_TAGLINE = "- Pure And Tasty -"
    const val RESTAURANT_DESCRIPTION = "Multi Cuisine Veg & Non-Veg Restaurant"
    const val RESTAURANT_EMAIL = "poposhighlight@gmail.com"
    const val RESTAURANT_SECONDARY_PHONE: String = "9597185001"
    const val RESTAURANT_PRIMARY_PHONE: String = "9500825077"
    const val RESTAURANT_ADDRESS = "Chinna Seeragapadi, Salem, TamilNadu, India 636308, Opp. of VIMS Hospital"


    //AddOn Screen Test Tags
    const val ADDON_SCREEN = "AddOn Screen"
    const val ADDON_NOT_AVAIlABLE = "AddOn Not Available"
    const val CREATE_NEW_ADDON_SCREEN = "Create New Addon Screen"
    const val CREATE_NEW_ADDON_BTN = "Create New Addon"


    //Common Constants
    const val SEARCH_ITEM_NOT_FOUND = "Searched Item Not Found"



}