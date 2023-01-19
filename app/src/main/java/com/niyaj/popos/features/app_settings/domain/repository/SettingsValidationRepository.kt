package com.niyaj.popos.features.app_settings.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface SettingsValidationRepository {

    fun validateCartInterval(cartsInterval: String): ValidationResult

    fun validateCartOrderInterval(cartOrderInterval: String): ValidationResult

    fun validateExpensesInterval(expensesInterval: String): ValidationResult

    fun validateReportsInterval(reportsInterval: String): ValidationResult
}