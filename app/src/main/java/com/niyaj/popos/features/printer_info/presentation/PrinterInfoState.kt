package com.niyaj.popos.features.printer_info.presentation

import com.niyaj.popos.features.printer_info.domain.model.Printer

sealed interface PrinterInfoState {
    object Loading : PrinterInfoState
    object Empty : PrinterInfoState
    data class Success(val info : Printer) : PrinterInfoState
}