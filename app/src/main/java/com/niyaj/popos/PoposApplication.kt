package com.niyaj.popos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.niyaj.popos.util.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.util.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_NAME
import com.niyaj.popos.util.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.util.Constants.GENERATE_REPORT_CHANNEL_NAME
import com.niyaj.popos.util.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import io.realm.kotlin.internal.interop.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import javax.inject.Inject

lateinit var applicationScope: CoroutineScope

@HiltAndroidApp
class PoposApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        createNotificationChannel(applicationContext, DELETE_DATA_NOTIFICATION_CHANNEL_ID, DELETE_DATA_NOTIFICATION_CHANNEL_NAME)

        createNotificationChannel(applicationContext, GENERATE_REPORT_CHANNEL_ID, GENERATE_REPORT_CHANNEL_NAME)

    }

}