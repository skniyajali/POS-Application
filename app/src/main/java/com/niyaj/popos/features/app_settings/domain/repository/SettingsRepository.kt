package com.niyaj.popos.features.app_settings.domain.repository

import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.common.util.Resource

interface SettingsRepository {

    fun getSetting(): Resource<Settings>

    suspend fun updateSetting(newSettings: Settings): Resource<Boolean>
}