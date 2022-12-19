package com.niyaj.popos.realm.app_settings.domain.use_cases

import com.niyaj.popos.realm.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.domain.model.Settings

class GetSetting(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Resource<Settings> {
        return settingsRepository.getSetting()
    }
}