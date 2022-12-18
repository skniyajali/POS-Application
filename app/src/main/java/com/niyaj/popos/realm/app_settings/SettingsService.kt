package com.niyaj.popos.realm.app_settings

import com.niyaj.popos.domain.util.Resource

interface SettingsService {

    fun getSetting(): Resource<SettingsRealm>

    suspend fun updateSetting(newSettings: SettingsRealm): Resource<Boolean>
}