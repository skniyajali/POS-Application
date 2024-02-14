package com.niyaj.feature.printer.add_edit

sealed interface UpdatePrinterInfoEvent {

    data class PrinterDpiChanged(val printerDpi : String) : UpdatePrinterInfoEvent
    data class PrinterWidthChanged(val printerWidth : String) : UpdatePrinterInfoEvent
    data class PrinterNbrLinesChanged(val printerNbrLines : String) : UpdatePrinterInfoEvent
    data class ProductNameLengthChanged(val length : String) : UpdatePrinterInfoEvent

    data class ProductReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent
    data class AddressReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent
    data class CustomerReportLimitChanged(val limit : String) : UpdatePrinterInfoEvent

    object PrintQrCodeChanged : UpdatePrinterInfoEvent
    object PrintResLogoChanged : UpdatePrinterInfoEvent
    object PrintWelcomeTextChanged : UpdatePrinterInfoEvent

    object UpdatePrinterInfo : UpdatePrinterInfoEvent
}