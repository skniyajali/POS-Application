package com.niyaj.feature.reminder

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ReminderTestTags.DELETE_REMINDER
import com.niyaj.common.tags.ReminderTestTags.DELETE_REMINDER_MSG
import com.niyaj.common.tags.ReminderTestTags.REMINDER_NOT_AVAILABLE
import com.niyaj.common.tags.ReminderTestTags.REMINDER_SCREEN
import com.niyaj.common.tags.ReminderTestTags.UPDATE_REMINDER
import com.niyaj.common.tags.ReminderTestTags.UPDATE_REMINDER_MSG
import com.niyaj.common.utils.toFormattedDateAndTime
import com.niyaj.designsystem.theme.LightColor12
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.reminder.destinations.EmployeeAbsentReminderScreenDestination
import com.niyaj.feature.reminder.destinations.EmployeeDailySalaryReminderScreenDestination
import com.niyaj.model.ReminderType
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import de.charlex.compose.RevealSwipe
import kotlinx.coroutines.launch

/**
 * Reminder Screen
 * @author Sk Niyaj Ali
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.REMINDER_SCREEN)
@Composable
fun ReminderScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    reminderViewModel: ReminderViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<EmployeeAbsentReminderScreenDestination, String>,
    dailySalaryRecipient: ResultRecipient<EmployeeDailySalaryReminderScreenDestination, String>
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val updateReminderState = rememberMaterialDialogState()
    val deleteReminderState = rememberMaterialDialogState()

    val uiState = reminderViewModel.state.collectAsStateWithLifecycle().value

    val selectedReminder = reminderViewModel.selectedReminder.collectAsStateWithLifecycle().value

    BackHandler(true) {
        if (selectedReminder.isNotEmpty()) {
            reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    dailySalaryRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackButton = true,
        title = REMINDER_SCREEN,
        selectionCount = 0,
        navActions = {},
        floatingActionButton = {}
    ) {
        Crossfade(
            targetState = uiState,
            label = "Reminders::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> ItemNotAvailable(text = REMINDER_NOT_AVAILABLE)

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        state = lazyListState,
                    ) {
                        items(
                            items = state.data,
                            key = { it.reminderId }
                        ) { reminder ->
                            RevealSwipe(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onContentClick = {
                                    when (reminder.reminderType) {
                                        ReminderType.Attendance.reminderType -> navController.navigate(
                                            EmployeeAbsentReminderScreenDestination
                                        )

                                        ReminderType.DailySalary.reminderType -> navController.navigate(
                                            EmployeeDailySalaryReminderScreenDestination
                                        )
                                    }
                                },
                                closeOnContentClick = true,
                                closeOnBackgroundClick = true,
                                maxRevealDp = 150.dp,
                                hiddenContentStart = {
                                    IconButton(
                                        onClick = {
                                            reminderViewModel.onEvent(
                                                ReminderEvent.SelectReminder(
                                                    reminder.reminderId
                                                )
                                            )
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
                                            reminderViewModel.onEvent(
                                                ReminderEvent.SelectReminder(
                                                    reminder.reminderId
                                                )
                                            )
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
        title(text = UPDATE_REMINDER)
        message(text = UPDATE_REMINDER_MSG)
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
        title(text = DELETE_REMINDER)
        message(text = DELETE_REMINDER_MSG)
    }

}