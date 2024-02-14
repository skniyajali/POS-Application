package com.niyaj.popos

import android.app.Application
import android.content.Context
import android.widget.Toast
import com.niyaj.worker.initializers.WorkInitializers
import dagger.hilt.android.HiltAndroidApp
import org.acra.config.mailSender
import org.acra.config.toast
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import timber.log.Timber

@HiltAndroidApp
class PoposApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        WorkInitializers.initialize(context = this)
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