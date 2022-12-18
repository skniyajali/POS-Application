package com.niyaj.popos.domain.use_cases.app_settings

import com.niyaj.popos.domain.repository.SettingsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.SettingsRealm

class GetSetting(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): Resource<SettingsRealm> {
        return settingsRepository.getSetting()
    }
}