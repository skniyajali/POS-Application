package com.niyaj.popos.util

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object Constants {

    // github login token - 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

    const val SPLASH_SCREEN_DURATION = 200L

    const val PRINTER_DPI = 176

    const val PRINTER_WIDTH_MM = 58f

    const val PRINTER_NBR_LINE = 31

    const val PRINTER_QR_DATA = "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

    const val REALM_DATABASE_NAME = "popos_highlight"

    const val REALM_PARTITION_NAME = "realm_partition"

    const val EXPENSE_CATEGORY_NAME = "Employee"

    const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 15

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
    val FILE_URI = MediaStore.Downloads.INTERNAL_CONTENT_URI

    const val PAID = "Paid"

    const val NOT_PAID = "Not Paid"

    const val DELETE_DATA_NOTIFICATION_CHANNEL_ID = "delete_data"
    const val DELETE_DATA_NOTIFICATION_CHANNEL_NAME = "Data Deletion"
    const val DELETE_DATA_INTERVAL_HOUR: Long = 15

    const val GENERATE_REPORT_CHANNEL_ID = "generate_report"
    const val GENERATE_REPORT_CHANNEL_NAME = "Generating Report"
    const val GENERATE_REPORT_INTERVAL_HOUR: Long = 1

    const val SETTINGS_ID = "11111111"
}