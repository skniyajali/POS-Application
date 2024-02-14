package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface SettingsValidationRepository {

    fun validateCartInterval(cartsInterval: String): ValidationResult

    fun validateCartOrderInterval(cartOrderInterval: String): ValidationResult

    fun validateExpensesInterval(expensesInterval: String): ValidationResult

    fun validateReportsInterval(reportsInterval: String): ValidationResult
}