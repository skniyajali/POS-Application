package com.niyaj.popos.features

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.hasNotificationPermission
import com.niyaj.popos.features.reminder.presentation.absent_reminder.EmployeeAbsentReminder
import com.niyaj.popos.features.reminder.presentation.daily_salary_reminder.DailySalaryReminderWorkerViewModel
import com.niyaj.popos.utils.Constants.DELETE_DATA_INTERVAL_HOUR
import com.niyaj.popos.utils.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_INTERVAL_HOUR
import com.niyaj.popos.utils.worker.DataDeletionWorker
import com.niyaj.popos.utils.worker.GenerateReportWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

/**
 * Main activity
 * @author Sk Niyaj Ali
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val workManager = WorkManager.getInstance(applicationContext)
        val absentReminder by viewModels<EmployeeAbsentReminder>()
        val dailySalaryReminder by viewModels<DailySalaryReminderWorkerViewModel>()

        val hasNotificationPermission = this.hasNotificationPermission()

        if (!hasNotificationPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    /* activity = */ this,
                    /* permissions = */ arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    /* requestCode = */ 0
                )
            }
        }

        val periodicDeletionWorker =
            PeriodicWorkRequestBuilder<DataDeletionWorker>(
                DELETE_DATA_INTERVAL_HOUR,
                TimeUnit.HOURS
            ).addTag(DELETE_DATA_NOTIFICATION_CHANNEL_ID).build()

        val generateReportWorker = PeriodicWorkRequestBuilder<GenerateReportWorker>(
            GENERATE_REPORT_INTERVAL_HOUR,
            TimeUnit.HOURS
        ).addTag(GENERATE_REPORT_CHANNEL_ID).build()

        workManager.enqueueUniquePeriodicWork(
            DELETE_DATA_NOTIFICATION_CHANNEL_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicDeletionWorker
        )

        workManager.enqueueUniquePeriodicWork(
            GENERATE_REPORT_CHANNEL_ID,
            ExistingPeriodicWorkPolicy.KEEP,
            generateReportWorker
        )

        setContent {
            PoposTheme {
                PoposApp(
                    workManager = workManager,
                    dataDeletionId = periodicDeletionWorker.id,
                    generateReportId = generateReportWorker.id,
                    absentReminderId = absentReminder.absentWorker.id,
                    dailySalaryReminderId = dailySalaryReminder.salaryWorker.id,
                )
            }
        }
    }
}