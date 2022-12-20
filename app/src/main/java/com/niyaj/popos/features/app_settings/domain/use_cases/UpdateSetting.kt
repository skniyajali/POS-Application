package com.niyaj.popos.features.app_settings.domain.use_cases

import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource

class UpdateSetting(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(newSettings: Settings): Resource<Boolean> {
        return settingsRepository.updateSetting(newSettings)
    }
}