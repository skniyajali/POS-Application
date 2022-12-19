package com.niyaj.popos.realm.app_settings.domain.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.app_settings.domain.model.Settings

interface SettingsRepository {

    fun getSetting(): Resource<Settings>

    suspend fun updateSetting(newSettings: Settings): Resource<Boolean>
}