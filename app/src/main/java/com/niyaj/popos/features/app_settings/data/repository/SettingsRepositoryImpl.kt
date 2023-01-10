package com.niyaj.popos.features.app_settings.data.repository

import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.util.Constants.SETTINGS_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class SettingsRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SettingsRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Settings Session")
    }


    override fun getSetting(): Resource<Settings> {
        return try {
            val settings = realm.query<Settings>().first().find()

            if (settings == null) {
                Resource.Success(Settings())
            }else {
                Resource.Success(settings)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get settings", Settings())
        }
    }

    override suspend fun updateSetting(newSettings: Settings): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val settings = this.query<Settings>("settingsId == $0", SETTINGS_ID).first().find()

                    if (settings != null) {
                        settings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                        settings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                        settings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                        settings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval
                        settings.updatedAt = System.currentTimeMillis().toString()
                    }else {
                        val createdSettings = Settings()
                        createdSettings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                        createdSettings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                        createdSettings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                        createdSettings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval

                        this.copyToRealm(createdSettings)
                    }
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create settings", false)
        }
    }
}