package com.niyaj.popos.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.presentation.components.util.SheetLayout
import com.niyaj.popos.presentation.ui.theme.PoposTheme
import com.niyaj.popos.presentation.util.NewNavigation
import com.niyaj.popos.util.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.util.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.util.Constants.GENERATE_REPORT_INTERVAL_HOUR
import com.niyaj.popos.util.worker.DataDeletionWorker
import com.niyaj.popos.util.worker.GenerateReportWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var currentBottomSheet = mutableStateOf<BottomSheetScreen?>(null)

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialNavigationApi::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADMIN,
            ),
            0
        )

        val currentTime = LocalDateTime.now().hour

        val periodicDeletionWorker =
            OneTimeWorkRequestBuilder<DataDeletionWorker>()
                .addTag(DELETE_DATA_NOTIFICATION_CHANNEL_ID)
                .build()

        val generateReportWorker = PeriodicWorkRequestBuilder<GenerateReportWorker>(
            GENERATE_REPORT_INTERVAL_HOUR, TimeUnit.HOURS
        ).addTag(GENERATE_REPORT_CHANNEL_ID).build()

        val workManager = WorkManager.getInstance(applicationContext)

        if(currentTime == 14){
            workManager.beginUniqueWork(
                DELETE_DATA_NOTIFICATION_CHANNEL_ID,
                ExistingWorkPolicy.APPEND,
                periodicDeletionWorker
            ).enqueue()
        }

        workManager.enqueueUniquePeriodicWork(
            GENERATE_REPORT_CHANNEL_ID,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            generateReportWorker
        )


        setContent {
            PoposTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val scaffoldState = rememberScaffoldState()
                    val navController = rememberAnimatedNavController()
                    val bottomSheetNavigator = rememberBottomSheetNavigator()
                    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
                    val scope = rememberCoroutineScope()
                    val context = LocalContext.current

                    workManager
                        .getWorkInfoByIdLiveData(periodicDeletionWorker.id)
                        .observe(this) { workInfo ->
                            when (workInfo.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    Timber.d("Data Deletion Successfully")
                                    Toast.makeText(
                                        context,
                                        "Data Deletion Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.FAILED -> {
                                    Timber.d("Unable to perform data deletion FAILED")
                                    Toast.makeText(
                                        context,
                                        "Unable to perform data deletion",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.CANCELLED -> {
                                    Timber.d("Data Deletion CANCELLED")
                                    Toast.makeText(
                                        context,
                                        "Data Deletion Cancelled",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                WorkInfo.State.ENQUEUED -> {
                                    Timber.d("Data Deletion ENQUEUED")
                                    Toast.makeText(
                                        context,
                                        "Data Deletion Enqueued",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.RUNNING -> {
                                    Timber.d("Data Deletion RUNNING")
                                    Toast.makeText(
                                        context,
                                        "Data Deletion Running",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.BLOCKED -> {
                                    Timber.d("Data Deletion BLOCKED")
                                    Toast.makeText(
                                        context,
                                        "Data Deletion Blocked",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }

                    workManager
                        .getWorkInfoByIdLiveData(generateReportWorker.id)
                        .observe(this) { workInfo ->
                            when (workInfo.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    Timber.d("Report Generated Successfully")
                                    Toast.makeText(
                                        context,
                                        "Report Generated Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.FAILED -> {
                                    Timber.d("Unable to generate report")
                                    Toast.makeText(
                                        context,
                                        "Unable to generate report",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.CANCELLED -> {
                                    Timber.d("Report Generate CANCELLED")
                                    Toast.makeText(
                                        context,
                                        "Report Generate Cancelled",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                                WorkInfo.State.ENQUEUED -> {
                                    Timber.d("Report Generate ENQUEUED")
                                    Toast.makeText(
                                        context,
                                        "Report Generate Enqueued",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.RUNNING -> {
                                    Timber.d("Report Generate RUNNING")
                                    Toast.makeText(
                                        context,
                                        "Report Generate Running",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                WorkInfo.State.BLOCKED -> {
                                    Timber.d("Report Generate BLOCKED")
                                    Toast.makeText(
                                        context,
                                        "Report Generate Blocked",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }

                    val systemUiController = rememberSystemUiController()
                    systemUiController.setStatusBarColor(
                        color = MaterialTheme.colors.primary,
                        darkIcons = false
                    )

                    // to set the current sheet to null when the bottom sheet closes
                    if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                        currentBottomSheet.value = null
                    }

                    val closeSheet: () -> Unit = {
                        scope.launch {
                            if (bottomSheetScaffoldState.bottomSheetState.isExpanded) {
                                bottomSheetScaffoldState.bottomSheetState.collapse()
                            }

                            // to set the current sheet to null when the bottom sheet closes
                            if (bottomSheetScaffoldState.bottomSheetState.isCollapsed) {
                                currentBottomSheet.value = null
                            }
                        }
                    }

                    val openSheet: (BottomSheetScreen) -> Unit = {
                        scope.launch {
                            currentBottomSheet.value = it
                            bottomSheetScaffoldState.bottomSheetState.expand()
                        }
                    }

                    BottomSheetScaffold(
                        sheetContent = {
                            currentBottomSheet.value?.let { currentSheet ->
                                SheetLayout(
                                    currentSheet,
                                    closeSheet,
                                )
                            }
                        },
                        sheetPeekHeight = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                        scaffoldState = bottomSheetScaffoldState,
                        sheetGesturesEnabled = true,
                        sheetElevation = 8.dp,
                        sheetShape = MaterialTheme.shapes.medium,
                    ) {
                        NewNavigation(
                            onOpenSheet = openSheet,
                            scaffoldState = scaffoldState,
                            bottomSheetScaffoldState = bottomSheetScaffoldState,
                            navController = navController,
                            bottomSheetNavigator = bottomSheetNavigator
                        )
                    }
                }
            }
        }
    }
}

// 168, 5, 8, 55,