package com.niyaj.popos.domain.use_cases.app_settings

import com.niyaj.popos.domain.repository.SettingsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.domain.model.Settings

class UpdateSetting(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(newSettings: Settings): Resource<Boolean> {
        return settingsRepository.updateSetting(newSettings)
    }
}