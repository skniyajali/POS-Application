package com.niyaj.popos.realm.app_settings

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realmApp
import com.niyaj.popos.util.Constants.SETTINGS_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class SettingsServiceImpl(config: SyncConfiguration) : SettingsService {

    private val user = realmApp.currentUser

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if (user == null && sessionState != "ACTIVE") {
            Timber.d("Settings: user is null")
        }

        Timber.d("Settings Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }


    override fun getSetting(): Resource<SettingsRealm> {
        return try {
            val settings = realm.query<SettingsRealm>().first().find()

            if (settings == null) {
                Resource.Success(SettingsRealm())
            }else {
                Resource.Success(settings)
            }
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get settings", SettingsRealm())
        }
    }

    override suspend fun updateSetting(newSettings: SettingsRealm): Resource<Boolean> {
        return try {
            realm.write {
                val settings = this.query<SettingsRealm>("_id == $0", SETTINGS_ID).first().find()

                if (settings != null) {
                    settings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                    settings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                    settings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                    settings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval
                    settings.updatedAt = System.currentTimeMillis().toString()
                }else {
                    val createdSettings = SettingsRealm()
                    createdSettings.expensesDataDeletionInterval = newSettings.expensesDataDeletionInterval
                    createdSettings.reportDataDeletionInterval = newSettings.reportDataDeletionInterval
                    createdSettings.cartDataDeletionInterval = newSettings.cartDataDeletionInterval
                    createdSettings.cartOrderDataDeletionInterval = newSettings.cartOrderDataDeletionInterval

                    this.copyToRealm(createdSettings)
                }
            }


            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create settings", false)
        }
    }
}