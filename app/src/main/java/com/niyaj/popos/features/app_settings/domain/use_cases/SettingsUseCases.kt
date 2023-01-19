package com.niyaj.popos.features.app_settings.domain.use_cases

import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateCartInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateCartOrderInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateExpensesInterval
import com.niyaj.popos.features.app_settings.domain.use_cases.validation.ValidateReportsInterval

data class SettingsUseCases(
    val getSetting: GetSetting,
    val updateSetting: UpdateSetting,
    val validateCartInterval: ValidateCartInterval,
    val validateCartOrderInterval: ValidateCartOrderInterval,
    val validateExpensesInterval: ValidateExpensesInterval,
    val validateReportsInterval: ValidateReportsInterval,
)
