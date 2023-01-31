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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AbsentReminderScreenDestination
import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.util.toFormattedDateAndTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Destination
@Composable
fun ReminderScreen(
    navController : NavController = rememberNavController(),
    scaffoldState : ScaffoldState = rememberScaffoldState(),
    reminderViewModel : ReminderViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val deleteProductState = rememberMaterialDialogState()
    val lazyListState = rememberLazyListState()

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

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
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

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = "Reminders")
        },
        isFloatingActionButtonDocked = reminders.isNotEmpty(),
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        floatingActionButton = {
            ExtendedFabButton(
                text = "",
                showScrollToTop = showScrollToTop.value,
                visible = selectedReminder.isNotEmpty() && reminders.isNotEmpty(),
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {},
            )
        },
        navActions = {
            if(selectedReminder.isNotEmpty()) {
                IconButton(
                    onClick = {},
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Product",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        deleteProductState.show()
                    },
                    enabled = selectedReminder.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Product",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        },
        navigationIcon = {
            if(selectedReminder.isNotEmpty()) {
                IconButton(
                    onClick = {
                        reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.close_icon),
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        },
        topAppBarBackgroundColor = backgroundColor,
    ) {
        MaterialDialog(
            dialogState = deleteProductState,
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
                        deleteProductState.hide()
                        reminderViewModel.onEvent(ReminderEvent.DeselectReminder)
                    },
                )
            }
        ) {
            title(text = "Delete Reminder?")
            message(res = R.string.delete_product_message)
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
                        Card(
                            onClick = {
                                if (reminder.reminderType == ReminderType.Attendance.reminderType) {
                                    navController.navigate(AbsentReminderScreenDestination)
                                }
                            },
                            enabled = !reminder.isCompleted && reminder.reminderType == ReminderType.Attendance.reminderType,
                            modifier = Modifier.fillMaxWidth(),
                            elevation = SpaceMini,
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

                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }
                }
            }
        }
    }
}