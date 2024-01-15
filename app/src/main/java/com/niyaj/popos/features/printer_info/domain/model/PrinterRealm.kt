package com.niyaj.popos.features.printer_info.domain.model

import com.niyaj.popos.common.utils.Constants.PRINTER_DPI
import com.niyaj.popos.common.utils.Constants.PRINTER_ID
import com.niyaj.popos.common.utils.Constants.PRINTER_NBR_LINE
import com.niyaj.popos.common.utils.Constants.PRINTER_WIDTH_MM
import com.niyaj.popos.common.utils.Constants.PRINT_ADDRESS_WISE_REPORT_LIMIT
import com.niyaj.popos.common.utils.Constants.PRINT_CUSTOMER_WISE_REPORT_LIMIT
import com.niyaj.popos.common.utils.Constants.PRINT_PRODUCT_WISE_REPORT_LIMIT
import com.niyaj.popos.common.utils.Constants.PRODUCT_NAME_LENGTH
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PrinterRealm : RealmObject {
    @PrimaryKey
    var printerId : String = PRINTER_ID

    var printerDpi : Int = PRINTER_DPI

    var printerWidth : Float = PRINTER_WIDTH_MM

    var printerNbrLines : Int = PRINTER_NBR_LINE

    var productNameLength : Int = PRODUCT_NAME_LENGTH

    var productWiseReportLimit : Int = PRINT_PRODUCT_WISE_REPORT_LIMIT

    var addressWiseReportLimit : Int = PRINT_ADDRESS_WISE_REPORT_LIMIT

    var customerWiseReportLimit : Int = PRINT_CUSTOMER_WISE_REPORT_LIMIT

    var printQRCode : Boolean = true

    var printResLogo : Boolean = true

    var printWelcomeText : Boolean = true

    var createdAt : String = System.currentTimeMillis().toString()

    var updatedAt : String? = null
}

fun PrinterRealm.toPrinter() : Printer {
    return Printer(
        printerId = this.printerId,
        printerDpi = this.printerDpi,
        printerWidth = this.printerWidth,
        printerNbrLines = this.printerNbrLines,
        productNameLength = this.productNameLength,
        productWiseReportLimit = this.productWiseReportLimit,
        addressWiseReportLimit = this.addressWiseReportLimit,
        customerWiseReportLimit = this.customerWiseReportLimit,
        printQRCode = this.printQRCode,
        printResLogo = this.printResLogo,
        printWelcomeText = this.printWelcomeText,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Printer.toPrinterRealm() : PrinterRealm {
    val newPrinter = PrinterRealm()
    newPrinter.printerId = this.printerId
    newPrinter.printerDpi = this.printerDpi
    newPrinter.printerWidth = this.printerWidth
    newPrinter.printerNbrLines = this.printerNbrLines
    newPrinter.productNameLength = this.productNameLength
    newPrinter.productWiseReportLimit = this.productWiseReportLimit
    newPrinter.addressWiseReportLimit = this.addressWiseReportLimit
    newPrinter.customerWiseReportLimit = this.customerWiseReportLimit
    newPrinter.printQRCode = this.printQRCode
    newPrinter.printResLogo = this.printResLogo
    newPrinter.printWelcomeText = this.printWelcomeText
    newPrinter.createdAt = this.createdAt
    newPrinter.updatedAt = this.updatedAt

    return newPrinter
}