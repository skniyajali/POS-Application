package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Settings

interface SettingsRepository {

    fun getSetting(): Resource<Settings>

    suspend fun updateSetting(newSettings: Settings): Resource<Boolean>
}