package com.niyaj.popos

import android.app.Application
import android.content.Context
import android.widget.Toast
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
import org.acra.config.notification
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

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        createNotificationChannel(applicationContext, DELETE_DATA_NOTIFICATION_CHANNEL_ID, DELETE_DATA_NOTIFICATION_CHANNEL_NAME)

        createNotificationChannel(applicationContext, GENERATE_REPORT_CHANNEL_ID, GENERATE_REPORT_CHANNEL_NAME)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            stopServicesOnCrash = false
            sendReportsInDevMode = true
            deleteUnapprovedReportsOnApplicationStart = true
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            //each plugin you chose above can be configured in a block like this:
//            Toast.makeText(this@PoposApplication, getString(R.string.toast_), Toast.LENGTH_SHORT).show()
            toast {
                //required
                text = getString(R.string.toast_text)
                //defaults to Toast.LENGTH_LONG
                length = Toast.LENGTH_LONG
            }

//            mailSender {
//                //required
//                mailTo = "niyaj639@gmail.com"
//                //defaults to true
//                reportAsFile = true
//                //defaults to ACRA-report.stacktrace
//                reportFileName = "Crash.txt"
//                //defaults to "<applicationId> Crash Report"
//                subject = getString(R.string.mail_subject)
//                //defaults to empty
//                body = getString(R.string.mail_body)
//            }

            notification {
                //required
                title = getString(R.string.notification_title)
                //required
                text = getString(R.string.notification_text)
                //required
                channelName = getString(R.string.notification_channel)
                //optional channel description
                channelDescription = getString(R.string.notification_channel_desc)
                //defaults to NotificationManager.IMPORTANCE_HIGH
//                resChannelImportance = NotificationManager.IMPORTANCE_MAX
                //optional, enables ticker text
//                tickerText = getString(R.string.notification_ticker)
                //defaults to android.R.drawable.stat_sys_warning
                resIcon = R.drawable.ic_clear
                //defaults to android.R.string.ok
                sendButtonText = getString(R.string.notification_send)
                //defaults to android.R.drawable.ic_menu_send
                //defaults to android.R.string.cancel
                discardButtonText = getString(R.string.notification_discard)
                //defaults to android.R.drawable.ic_menu_delete
                //optional, enables inline comment button
//                sendWithCommentButtonText = getString(R.string.notification_send_with_comment)
                //required if above is set
//                resSendWithCommentButtonIcon = R.drawable.notification_send_with_comment
                //optional inline comment hint
//                commentPrompt = getString(R.string.notification_comment)
                //defaults to false
                sendOnClick = true
            }
        }
    }
}