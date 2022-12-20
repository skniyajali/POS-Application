package com.niyaj.popos.features.app_settings.domain.use_cases

import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource

class GetSetting(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Resource<Settings> {
        return settingsRepository.getSetting()
    }
}