package com.niyaj.popos.features.app_settings.domain.use_cases.validation

import com.niyaj.popos.features.app_settings.domain.repository.SettingsValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateExpensesInterval @Inject constructor(
    private val settingsValidationRepository: SettingsValidationRepository
) {

    operator fun invoke(expensesInterval: String): ValidationResult {
        return settingsValidationRepository.validateExpensesInterval(expensesInterval)
    }
}