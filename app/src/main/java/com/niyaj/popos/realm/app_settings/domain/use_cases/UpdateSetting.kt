package com.niyaj.popos.realm.app_settings.domain.use_cases

import com.niyaj.popos.realm.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.domain.model.Settings

class UpdateSetting(private val settingsRepository: SettingsRepository) {

    suspend operator fun invoke(newSettings: Settings): Resource<Boolean> {
        return settingsRepository.updateSetting(newSettings)
    }
}