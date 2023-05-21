package com.niyaj.popos.features

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.features.common.util.Navigation
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import io.sentry.compose.withSentryObservableEffect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun PoposApp(
    workManager: WorkManager,
    dataDeletionId: UUID,
    generateReportId: UUID,
    absentReminderId: UUID,
    dailySalaryReminderId: UUID
) {
    // A surface container using the 'background' color from the theme
    val scaffoldState = rememberScaffoldState()
    val navController = rememberAnimatedNavController()
        .withSentryObservableEffect(
            enableNavigationBreadcrumbs = true,
            enableNavigationTracing = true
        )
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val bottomSheetNavigator = remember { BottomSheetNavigator(sheetState) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    val deletionState = workManager
        .getWorkInfoByIdLiveData(dataDeletionId)
        .observeAsState()

    LaunchedEffect(key1 = deletionState) {
        deletionState.value?.let { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.FAILED -> {
                    Toast.makeText(
                        context,
                        "Unable to perform data deletion",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                WorkInfo.State.RUNNING -> {
                    Toast.makeText(
                        context,
                        "Data Deletion Running",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {}
            }
        }
    }

    val reportState = workManager
        .getWorkInfoByIdLiveData(generateReportId)
        .observeAsState()

    LaunchedEffect(key1 = reportState) {
        reportState.value?.let { workInfo ->
            when (workInfo.state) {
                WorkInfo.State.FAILED -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "Unable to generate report"
                        )
                    }
                }
                WorkInfo.State.RUNNING -> {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "Report Generate Running"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    Timber.d("Reminders - $absentReminderId & $dailySalaryReminderId")

//    val absentReminderWorker = workManager
//        .getWorkInfoByIdLiveData(absentReminderId)
//        .observeAsState().value
//
//    val dailyReminderWorker = workManager
//        .getWorkInfoByIdLiveData(dailySalaryReminderId)
//        .observeAsState().value
//
//    LaunchedEffect(key1 = Unit, key2 = absentReminderWorker) {
//        if (absentReminderWorker != null) {
//            when(absentReminderWorker.state) {
//                WorkInfo.State.ENQUEUED -> {
//                    Timber.d("Employee Absent Reminder Enqueued")
//                }
//                WorkInfo.State.RUNNING -> {
//                    val reminder = employeeAbsentReminder.reminder.value
//
//                    if (!reminder.isCompleted && currentTime in reminder.reminderStartTime .. reminder.reminderEndTime) {
//                        navController.navigate(EmployeeAbsentReminderScreenDestination)
//                    }else {
//                        workManager.cancelWorkById(employeeAbsentReminder.absentWorker.id)
//                    }
//                }
//                else -> {}
//            }
//        }
//    }
//
//    LaunchedEffect(key1 = Unit, key2 = dailyReminderWorker) {
//        if (dailyReminderWorker != null) {
//            when(dailyReminderWorker.state) {
//                WorkInfo.State.ENQUEUED -> {
//                    Timber.d("Daily Salary Reminder Enqueued")
//                }
//                WorkInfo.State.RUNNING -> {
//                    Timber.d("Daily Salary Reminder Running")
//                    val reminder = dailySalaryReminderWorkerViewModel.salaryReminder.value
//
//                    if (!reminder.isCompleted && currentTime in reminder.reminderStartTime .. reminder.reminderEndTime) {
//                        navController.navigate(EmployeeDailySalaryReminderScreenDestination)
//                    }
//                }
//                else -> {}
//            }
//        }
//    }

    systemUiController.setStatusBarColor(
        color = MaterialTheme.colors.primary,
        darkIcons = false
    )

    Navigation(
        scaffoldState = scaffoldState,
        navController = navController,
        bottomSheetNavigator = bottomSheetNavigator,
        startRoute = MainFeedScreenDestination,
    )
}