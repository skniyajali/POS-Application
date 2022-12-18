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
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.exceptions.AuthException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.ServiceException
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


lateinit var realmApp: App

@HiltAndroidApp
class PoposApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Timber.d("application started")


        realmApp = App.create(AppConfiguration.create(getString(R.string.realm_app_id)))

        Timber.d("Initialized the Realm App configuration for: ${realmApp.configuration.appId}")

        val credentials: Credentials = Credentials.emailPassword(
            email = getString(R.string.realm_user_email),
            password = getString(R.string.realm_user_password)
        )

        var user: User?


        MainScope().launch {

            Timber.d("login user")

            try {
                user = if (realmApp.currentUser == null) {
                    realmApp.login(credentials)
                } else {
                    Timber.d("user is already logged in")
                    realmApp.currentUser
                }

                Timber.d("login successfully user = $user")

            } catch (e: InvalidCredentialsException) {
                Timber.e(e.message ?: "InvalidCredentialsException")
            } catch (e: AuthException) {
                Timber.e(e.message ?: "AuthException")
            } catch (e: ServiceException) {
                Timber.e(e.message ?: "ServiceException")
            } catch (e: Exception) {
                Timber.e(e.message ?: "Authentication failed")
            }
        }

        createNotificationChannel(applicationContext, DELETE_DATA_NOTIFICATION_CHANNEL_ID, DELETE_DATA_NOTIFICATION_CHANNEL_NAME)

        createNotificationChannel(applicationContext, GENERATE_REPORT_CHANNEL_ID, GENERATE_REPORT_CHANNEL_NAME)

    }

}