package com.niyaj.popos

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.niyaj.popos.utils.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.utils.Constants.ABSENT_REMINDER_NAME
import com.niyaj.popos.utils.Constants.DAILY_SALARY_REMINDER_ID
import com.niyaj.popos.utils.Constants.DAILY_SALARY_REMINDER_NAME
import com.niyaj.popos.utils.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.utils.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_NAME
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_CHANNEL_NAME
import com.niyaj.popos.utils.createNotificationChannel
import dagger.hilt.android.HiltAndroidApp
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.log.RealmLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber
import javax.inject.Inject

lateinit var applicationScope: CoroutineScope

@HiltAndroidApp
class PoposApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration()=
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())

            RealmLog.level =  LogLevel.ALL
            RealmLog.addDefaultSystemLogger()
        }

        applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        createNotificationChannel(applicationContext, DELETE_DATA_NOTIFICATION_CHANNEL_ID, DELETE_DATA_NOTIFICATION_CHANNEL_NAME)

        createNotificationChannel(applicationContext, GENERATE_REPORT_CHANNEL_ID, GENERATE_REPORT_CHANNEL_NAME)

        createNotificationChannel(applicationContext, ABSENT_REMINDER_ID, ABSENT_REMINDER_NAME)

        createNotificationChannel(applicationContext, DAILY_SALARY_REMINDER_ID, DAILY_SALARY_REMINDER_NAME)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            stopServicesOnCrash = false
            sendReportsInDevMode = false
            deleteUnapprovedReportsOnApplicationStart = true
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            toast {
                //required
                text = getString(R.string.toast_text)
                //defaults to Toast.LENGTH_LONG
                length = Toast.LENGTH_LONG
            }

            mailSender {
                //required
                mailTo = "niyaj639@gmail.com"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = getString(R.string.mail_subject)
                //defaults to empty
                body = getString(R.string.mail_body)
            }
        }
    }
}

/*

 A cutting-edge POS application designed specifically for restaurants!
 This innovative software streamlines the ordering process, allowing for multiple types of orders,
 whether it be dine-in or take-out. Not only that, but it also assists in managing daily expenses,
  generating bills, and keeping track of employee and their salaries.
  With its advanced reporting capabilities, this application provides
  valuable insights into different types of sales,
  giving restaurant owners the tools they need to make informed decisions and drive success.
 */

/*
    Name Cheeap - RppQZ4Ms2q
 */