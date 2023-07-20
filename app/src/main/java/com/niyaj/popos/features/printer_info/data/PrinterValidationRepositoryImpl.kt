package com.niyaj.popos.features.printer_info.data

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.printer_info.domain.repository.PrinterValidationRepository

class PrinterValidationRepositoryImpl : PrinterValidationRepository {

    override fun validatePrinterDpi(dpi : Int) : ValidationResult {
        if (dpi <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Printer dpi is required."
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrinterWidth(width : Float) : ValidationResult {
        if (width <= 0f) {
            return ValidationResult(
                successful = false,
                errorMessage = "Printer width is required."
            )
        }

        return ValidationResult(true)
    }

    override fun validatePrinterNbrLines(lines : Int) : ValidationResult {
        if (lines <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Printer NBR line length is required."
            )
        }

        return ValidationResult(true)
    }

    override fun validateProductNameLength(length : Int) : ValidationResult {
        if (length <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name length is required."
            )
        }

        if (length <= 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name length must be more than ten character."
            )
        }

        return ValidationResult(true)
    }

    override fun validateProductReportLimit(limit : Int) : ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product report limit is required."
            )
        }

        if (limit < 20) {
            return ValidationResult(
                successful = false,
                errorMessage = "Product name limit must be more than 20."
            )
        }

        return ValidationResult(true)
    }

    override fun validateAddressReportLimit(limit : Int) : ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address report limit is required."
            )
        }

        if (limit < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Address name limit must be more than 10."
            )
        }

        return ValidationResult(true)
    }

    override fun validateCustomerReportLimit(limit : Int) : ValidationResult {
        if (limit <= 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "Customer report limit is required."
            )
        }

        if (limit < 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "Customer name limit must be more than 10."
            )
        }

        return ValidationResult(true)
    }
}