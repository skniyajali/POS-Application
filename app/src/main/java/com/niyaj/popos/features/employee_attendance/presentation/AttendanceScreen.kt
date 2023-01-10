package com.niyaj.popos.features.employee_attendance.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.AddEditPartnerScreenDestination
import com.niyaj.popos.features.destinations.SalaryScreenDestination
import com.niyaj.popos.util.toFormattedDate
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Destination
@Composable
fun AttendanceScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>,
) {

    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val attendances = attendanceViewModel.attendance.collectAsStateWithLifecycle().value.attendances
    val isLoading = attendanceViewModel.attendance.collectAsStateWithLifecycle().value.isLoading
    val hasError = attendanceViewModel.attendance.collectAsStateWithLifecycle().value.error

    val groupedEmployeeAbsent = attendances.groupBy { it.employee }

    val selectedAttendance = attendanceViewModel.selectedAttendance.collectAsStateWithLifecycle().value
    val selectedEmployee = attendanceViewModel.selectedEmployee.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedAttendance.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if (isContextualMode) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.primary
        }
    }

    val showSearchBar = attendanceViewModel.toggledSearchBar.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        attendanceViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.errorMessage)
                }

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    BackHandler(true) {
        if (showSearchBar) {
            attendanceViewModel.onSearchBarCloseAndClearClick()
        } else if (selectedAttendance.isNotEmpty()) {
            attendanceViewModel.onEvent(AttendanceEvent.SelectAttendance(selectedAttendance))
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedAttendance.isNotEmpty()) {
                    attendanceViewModel.onEvent(AttendanceEvent.SelectAttendance(selectedAttendance))
                }
            }
            is NavResult.Value -> {
                if (selectedAttendance.isNotEmpty()) {
                    attendanceViewModel.onEvent(AttendanceEvent.SelectAttendance(selectedAttendance))
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        onBackButtonClick = {
            if (showSearchBar) {
                attendanceViewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigateUp()
            }
        },
        title = {
            Text(text = "Absent Reports")
        },
        isFloatingActionButtonDocked = attendances.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_absent_entry).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = attendances.isNotEmpty() && selectedAttendance.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditAbsentScreenDestination(attendanceId = selectedAttendance))
                },
            )
        },
        floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if (selectedAttendance.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditAbsentScreenDestination(attendanceId = selectedAttendance, employeeId = selectedEmployee))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Absent Reports",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                    enabled = selectedAttendance.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Absent Reports",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            } else if (showSearchBar) {
                StandardSearchBar(
                    searchText = attendanceViewModel.searchText.collectAsStateWithLifecycle().value,
                    placeholderText = "Search for employees...",
                    onSearchTextChanged = {
                        attendanceViewModel.onEvent(AttendanceEvent.OnSearchAttendance(it))
                    },
                    onClearClick = {
                        attendanceViewModel.onSearchTextClearClick()
                    },
                )
            } else {
                if (attendances.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            attendanceViewModel.onEvent(AttendanceEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate(SalaryScreenDestination)
                    }
                ) {
                    Icon(imageVector = Icons.Default.Money, contentDescription = "Open Salary Screen")
                }
            }
        },
        navigationIcon = {},
        topAppBarBackgroundColor = backgroundColor,
    ) {
        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton(
                    text = "Delete",
                    onClick = {
                        attendanceViewModel.onEvent(
                            AttendanceEvent.DeleteAttendance(
                                selectedAttendance
                            )
                        )
                    }
                )
                negativeButton(
                    text = "Cancel",
                    onClick = {
                        dialogState.hide()
                    },
                )
            }
        ) {
            title(text = "Delete Absent Report?")
            message(res = R.string.delete_absent_message)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                attendanceViewModel.onEvent(AttendanceEvent.RefreshAttendance)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
            ) {
                if (attendances.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError
                            ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(
                                id = R.string.no_items_in_absent),
                        buttonText = stringResource(id = R.string.create_absent_entry).uppercase(),
                        onClick = {
                            navController.navigate(AddEditPartnerScreenDestination())
                        }
                    )
                } else {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        item(key = "employeeAbsents") {
                            groupedEmployeeAbsent.forEach { (employee, employeeAttendances) ->
                                if(employee != null){
                                    Card(
                                        onClick = {
                                            attendanceViewModel.onEvent(
                                                AttendanceEvent.SelectEmployee(employee.employeeId)
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(4.dp),
                                        elevation = SpaceMini
                                    ) {
                                        StandardExpandable(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                            expanded = selectedEmployee == employee.employeeId,
                                            onExpandChanged = {
                                                attendanceViewModel.onEvent(
                                                    AttendanceEvent.SelectEmployee(employee.employeeId)
                                                )
                                            },
                                            title = {
                                                TextWithIcon(
                                                    text = employee.employeeName,
                                                    icon = Icons.Default.Person,
                                                    isTitle = true
                                                )
                                            },
                                            trailing = {
                                                IconBox(
                                                    text = "Add Entry",
                                                    icon = Icons.Default.Add,
                                                    onClick = {
                                                        navController.navigate(
                                                            AddEditAbsentScreenDestination(employeeId = employee.employeeId)
                                                        )
                                                    }
                                                )
                                            },
                                            rowClickable = true,
                                            expand = { modifier: Modifier ->
                                                IconButton(
                                                    modifier = modifier,
                                                    onClick = {
                                                        attendanceViewModel.onEvent(
                                                            AttendanceEvent.SelectEmployee(employee.employeeId)
                                                        )
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                                        contentDescription = "Expand More",
                                                        tint = MaterialTheme.colors.secondary
                                                    )
                                                }
                                            },
                                            content = {
                                                FlowRow(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(SpaceMedium),
                                                    crossAxisSpacing = SpaceMini,
                                                ) {
                                                    employeeAttendances.forEach{  attendance ->
                                                        Card(
                                                            onClick = {
                                                                attendanceViewModel.onEvent(
                                                                    AttendanceEvent.SelectAttendance(
                                                                        attendance.attendeeId
                                                                    )
                                                                )
                                                            },
                                                            backgroundColor = LightColor6,
                                                            elevation = if (selectedAttendance == attendance.attendeeId) 2.dp else 0.dp,
                                                            border = if (selectedAttendance == attendance.attendeeId) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
                                                        ) {
                                                            Text(
                                                                text = attendance.absentDate.toFormattedDate,
                                                                style = MaterialTheme.typography.body1,
                                                                textAlign = TextAlign.Start,
                                                                fontWeight = FontWeight.SemiBold,
                                                                modifier = Modifier.padding(
                                                                    SpaceSmall
                                                                )
                                                            )
                                                        }

                                                        Spacer(modifier = Modifier.width(SpaceSmall))
                                                    }
                                                }
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(SpaceMedium))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}