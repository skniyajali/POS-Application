package com.niyaj.popos.features.reminder.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.EmployeeAbsentReminderScreenDestination
import com.niyaj.popos.features.destinations.EmployeeDailySalaryReminderScreenDestination
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.features.reminder.presentation.absent_reminder.EmployeeAbsentReminder
import com.niyaj.popos.features.reminder.presentation.daily_salary_reminder.DailySalaryReminderWorkerViewModel
import com.niyaj.popos.utils.toFormattedDateAndTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import de.charlex.compose.RevealSwipe
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Reminder Screen
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ReminderScreen(
    navController : NavController = rememberNavController(),
    scaffoldState : ScaffoldState = rememberScaffoldState(),
    reminderViewModel : ReminderViewModel = hiltViewModel(),
    employeeAbsentReminder: EmployeeAbsentReminder = hiltViewModel(),
    dailySalaryReminderWorkerViewModel: DailySalaryReminderWorkerViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<EmployeeAbsentReminderScreenDestination, String>,
    dailySalaryRecipient: ResultRecipient<EmployeeDailySalaryReminderScreenDestination, String>
) {
    val context = LocalContext.current
    val workManager = WorkManager.getInstance(context)
    val currentTime = System.currentTimeMillis().toString()

    val scope = rememberCoroutineScope()
    val updateReminderState = rememberMaterialDialogState()
    val deleteReminderState = rememberMaterialDialogState()

    val reminders = reminderViewModel.state.collectAsStateWithLifecycle().value.reminders
    val isLoading = reminderViewModel.state.collectAsStateWithLifecycle().value.isLoading
    val hasError = reminderViewModel.state.collectAsStateWithLifecycle().value.hasErrors

    val selectedReminder = reminderViewModel.selectedReminder.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedReminder.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    BackHandler(true) {
        if(selectedReminder.isNotEmpty()) {
            reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
        } else{
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    dailySalaryRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    val absentReminderWorker = workManager
        .getWorkInfoByIdLiveData(employeeAbsentReminder.absentWorker.id)
        .observeAsState().value

    val dailyReminderWorker = workManager
        .getWorkInfoByIdLiveData(dailySalaryReminderWorkerViewModel.salaryWorker.id)
        .observeAsState().value

    LaunchedEffect(key1 = Unit, key2 = absentReminderWorker) {
        if (absentReminderWorker != null) {
            when(absentReminderWorker.state) {
                WorkInfo.State.ENQUEUED -> {
                    Timber.d("Employee Absent Reminder Enqueued")
                }
                WorkInfo.State.RUNNING -> {
                    Timber.d("Employee Absent Reminder Running")
                    val reminder = employeeAbsentReminder.reminder.value

                    if (!reminder.isCompleted && currentTime in reminder.reminderStartTime .. reminder.reminderEndTime) {
                        navController.navigate(EmployeeAbsentReminderScreenDestination)
                    }else {
                        workManager.cancelWorkById(employeeAbsentReminder.absentWorker.id)
                    }
                }
                else -> {}
            }
        }
    }

    LaunchedEffect(key1 = Unit, key2 = dailyReminderWorker) {
        if (dailyReminderWorker != null) {
            when(dailyReminderWorker.state) {
                WorkInfo.State.ENQUEUED -> {
                    Timber.d("Daily Salary Reminder Enqueued")
                }
                WorkInfo.State.RUNNING -> {
                    Timber.d("Daily Salary Reminder Running")
                    val reminder = dailySalaryReminderWorkerViewModel.salaryReminder.value

                    if (!reminder.isCompleted && currentTime in reminder.reminderStartTime .. reminder.reminderEndTime) {
                        navController.navigate(EmployeeDailySalaryReminderScreenDestination)
                    }
                }
                else -> {}
            }
        }
    }

    SentryTraced(tag = "ReminderScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            title = {
                Text(text = "Reminders")
            },
            topAppBarBackgroundColor = backgroundColor,
        ) {
            MaterialDialog(
                dialogState = updateReminderState,
                buttons = {
                    positiveButton(
                        text = "Update",
                        onClick = {
                            reminderViewModel.onEvent(ReminderEvent.UpdateReminder(selectedReminder))
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            updateReminderState.hide()
                            reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
                        },
                    )
                }
            ) {
                title(text = "Update Reminder?")
                message(res = R.string.update_reminder_state)
            }

            MaterialDialog(
                dialogState = deleteReminderState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            reminderViewModel.onEvent(ReminderEvent.DeleteReminder(selectedReminder))
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteReminderState.hide()
                            reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
                        },
                    )
                }
            ) {
                title(text = "Delete Reminder?")
                message(res = R.string.delete_reminder)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    reminderViewModel.onEvent(ReminderEvent.RefreshReminder)
                }
            ) {
                if ((reminders.isEmpty() && selectedReminder.isEmpty()) || hasError != null) {
                    ItemNotAvailable(
                        text = hasError ?: "Reminders not available",
                        buttonText = "",
                        onClick = {}
                    )
                }
                else if(isLoading){
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        CircularProgressIndicator()
                    }
                }
                else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                    ) {
                        itemsIndexed(reminders) { _, reminder ->
                            RevealSwipe(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onContentClick = {
                                    when(reminder.reminderType) {
                                        ReminderType.Attendance.reminderType -> navController.navigate(EmployeeAbsentReminderScreenDestination)
                                        ReminderType.DailySalary.reminderType -> navController.navigate(EmployeeDailySalaryReminderScreenDestination)
                                        else -> {}
                                    }
                                },
                                closeOnContentClick = true,
                                closeOnBackgroundClick = true,
                                maxRevealDp = 150.dp,
                                hiddenContentStart = {
                                    IconButton(
                                        onClick = {
                                            reminderViewModel.onEvent(ReminderEvent.SelectReminder(reminder.reminderId))
                                            updateReminderState.show()
                                        },
                                        modifier = Modifier,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Update,
                                            contentDescription = "Update Reminder State",
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }

                                },
                                hiddenContentEnd = {
                                    IconButton(
                                        onClick = {
                                            reminderViewModel.onEvent(ReminderEvent.SelectReminder(reminder.reminderId))
                                            deleteReminderState.show()
                                        },
                                        modifier = Modifier
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Reminder",
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }
                                },
                                contentColor = MaterialTheme.colors.primary,
                                backgroundCardContentColor = LightColor12,
                                backgroundCardStartColor = MaterialTheme.colors.primary,
                                backgroundCardEndColor = MaterialTheme.colors.error,
                                shape = RoundedCornerShape(4.dp),
                                backgroundStartActionLabel = "Start",
                                backgroundEndActionLabel = "End",
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    elevation = SpaceMini,
                                    shape = it,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.SpaceBetween,
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            TextWithIcon(
                                                text = reminder.reminderName,
                                                icon = Icons.Default.EventBusy,
                                                isTitle = true,
                                                tintColor = if (reminder.isCompleted) MaterialTheme.colors.secondaryVariant else MaterialTheme.colors.primary
                                            )
                                            Spacer(modifier = Modifier.width(SpaceSmall))
                                            TextWithIcon(
                                                text = "${reminder.reminderInterval} ${reminder.reminderIntervalTimeUnit}",
                                                icon = Icons.Default.HourglassEmpty,
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            TextWithIcon(
                                                text = reminder.reminderStartTime.toFormattedDateAndTime,
                                                icon = Icons.Default.Update,
                                            )
                                            Spacer(modifier = Modifier.width(SpaceSmall))

                                            TextWithIcon(
                                                text = reminder.reminderEndTime.toFormattedDateAndTime,
                                                icon = Icons.Default.Restore,
                                            )

                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}