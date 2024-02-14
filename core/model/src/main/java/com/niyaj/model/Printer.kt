package com.niyaj.model

import com.niyaj.common.utils.Constants

data class Printer(
    val printerId : String = Constants.PRINTER_ID,

    val printerDpi : Int = Constants.PRINTER_DPI,

    val printerWidth : Float = Constants.PRINTER_WIDTH_MM,

    val printerNbrLines : Int = Constants.PRINTER_NBR_LINE,

    val productNameLength : Int = Constants.PRODUCT_NAME_LENGTH,

    val productWiseReportLimit : Int = Constants.PRINT_PRODUCT_WISE_REPORT_LIMIT,

    val addressWiseReportLimit : Int = Constants.PRINT_ADDRESS_WISE_REPORT_LIMIT,

    val customerWiseReportLimit : Int = Constants.PRINT_CUSTOMER_WISE_REPORT_LIMIT,

    val printQRCode : Boolean = true,

    val printResLogo : Boolean = true,

    val printWelcomeText : Boolean = true,

    val createdAt : String = System.currentTimeMillis().toString(),

    val updatedAt : String? = null,
)
