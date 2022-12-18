package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.SettingsRealm

interface SettingsRepository {

    fun getSetting(): Resource<SettingsRealm>

    suspend fun updateSetting(newSettings: SettingsRealm): Resource<Boolean>
}