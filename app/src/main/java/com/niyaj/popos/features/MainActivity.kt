package com.niyaj.popos.features

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.hasNotificationPermission
import com.niyaj.popos.features.reminder.presentation.absent_reminder.EmployeeAbsentReminder
import com.niyaj.popos.features.reminder.presentation.daily_salary_reminder.DailySalaryReminderWorkerViewModel
import com.niyaj.popos.utils.Constants.DELETE_DATA_INTERVAL_HOUR
import com.niyaj.popos.utils.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.utils.Constants.GENERATE_REPORT_INTERVAL_HOUR
import com.niyaj.popos.utils.Constants.MY_REQUEST_CODE
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

    private lateinit var appUpdateManager : AppUpdateManager
    private val updateOptions = AppUpdateOptions
        .newBuilder(AppUpdateType.IMMEDIATE)
        .setAllowAssetPackDeletion(false)
        .build()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        appUpdateManager = AppUpdateManagerFactory.create(this)
        checkForAppUpdates()

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

        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            {
                if (result.resultCode != RESULT_OK) {
                    Toast.makeText(
                        this,
                        "Something Went Wrong!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


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

    override fun onResume() {
        super.onResume()

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        updateOptions,
                        MY_REQUEST_CODE
                    )
                }
            }
    }

    private fun checkForAppUpdates() {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { info ->
                val isUpdateAvailable =
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                val isUpdateAllowed = when (updateOptions.appUpdateType()) {
                    AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                    else -> false
                }

                if (isUpdateAvailable && isUpdateAllowed) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        this,
                        updateOptions,
                        MY_REQUEST_CODE
                    )
                }

            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Unable to update app!",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}