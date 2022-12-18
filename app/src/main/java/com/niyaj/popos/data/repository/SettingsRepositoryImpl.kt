package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.repository.SettingsRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.SettingsRealm
import com.niyaj.popos.realm.app_settings.SettingsService

class SettingsRepositoryImpl(
    private val settingsService: SettingsService
) : SettingsRepository {

    override fun getSetting(): Resource<SettingsRealm> {
        return settingsService.getSetting()
    }

    override suspend fun updateSetting(newSettings: SettingsRealm): Resource<Boolean> {
        return settingsService.updateSetting(newSettings)
    }
}