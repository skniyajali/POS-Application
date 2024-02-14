package com.niyaj.data.mapper

import com.niyaj.database.model.PrinterEntity
import com.niyaj.model.Printer

fun Printer.toPrinterRealm() : PrinterEntity {
    val newPrinter = PrinterEntity()
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