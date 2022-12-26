package com.niyaj.popos.features

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
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.features.common.ui.theme.PoposTheme
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.common.util.hasBluetoothPermission
import com.niyaj.popos.features.common.util.hasStoragePermission
import com.niyaj.popos.features.components.util.SheetLayout
import com.niyaj.popos.util.Constants.DELETE_DATA_INTERVAL_HOUR
import com.niyaj.popos.util.Constants.DELETE_DATA_NOTIFICATION_CHANNEL_ID
import com.niyaj.popos.util.Constants.GENERATE_REPORT_CHANNEL_ID
import com.niyaj.popos.util.Constants.GENERATE_REPORT_INTERVAL_HOUR
import com.niyaj.popos.util.worker.DataDeletionWorker
import com.niyaj.popos.util.worker.GenerateReportWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
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
//        window.setDecorFitsSystemWindows(false)

        val hasBluetoothPermission = applicationContext.hasBluetoothPermission()
        val hasStoragePermission = applicationContext.hasStoragePermission()

        if (!hasBluetoothPermission) {
            ActivityCompat.requestPermissions(
                /* activity = */ this,
                /* permissions = */ arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADMIN,

                    ),
                /* requestCode = */ 0
            )
        }

        if (!hasStoragePermission) {
            ActivityCompat.requestPermissions(
                /* activity = */ this,
                /* permissions = */ arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    ),
                /* requestCode = */ 0
            )
        }

        val periodicDeletionWorker =
            PeriodicWorkRequestBuilder<DataDeletionWorker>(
                24, TimeUnit.HOURS, DELETE_DATA_INTERVAL_HOUR, TimeUnit.HOURS
            ).addTag(DELETE_DATA_NOTIFICATION_CHANNEL_ID).build()

        val generateReportWorker = PeriodicWorkRequestBuilder<GenerateReportWorker>(
            GENERATE_REPORT_INTERVAL_HOUR, TimeUnit.HOURS
        ).addTag(GENERATE_REPORT_CHANNEL_ID).build()

        val workManager = WorkManager.getInstance(applicationContext)

        workManager.enqueueUniquePeriodicWork(
            DELETE_DATA_NOTIFICATION_CHANNEL_ID,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            periodicDeletionWorker
        )

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
                    modifier = Modifier
                        .fillMaxSize()
//                        .statusBarsPadding()
//                        .navigationBarsPadding()
//                        .imePadding(),
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
                                }

                                WorkInfo.State.ENQUEUED -> {
                                    Timber.d("Data Deletion ENQUEUED")
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
                                }
                            }
                        }

                    workManager
                        .getWorkInfoByIdLiveData(generateReportWorker.id)
                        .observe(this) { workInfo ->
                            when (workInfo.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    Timber.d("Report Generated Successfully")
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Report Generated Successfully"
                                        )
                                    }
                                }

                                WorkInfo.State.FAILED -> {
                                    Timber.d("Unable to generate report")
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Unable to generate report"
                                        )
                                    }
                                }

                                WorkInfo.State.CANCELLED -> {
                                    Timber.d("Report Generate CANCELLED")
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Report Generate Cancelled",
                                        )
                                    }
                                }

                                WorkInfo.State.ENQUEUED -> {
                                    Timber.d("Report Generate ENQUEUED")
                                }

                                WorkInfo.State.RUNNING -> {
                                    Timber.d("Report Generate RUNNING")
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Report Generate Running"
                                        )
                                    }
                                }

                                WorkInfo.State.BLOCKED -> {
                                    Timber.d("Report Generate BLOCKED")
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            "Report Generate Blocked"
                                        )
                                    }
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
                                    currentScreen = currentSheet,
                                    onCloseBottomSheet = closeSheet,
                                    navController = navController,
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
                        Navigation(
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