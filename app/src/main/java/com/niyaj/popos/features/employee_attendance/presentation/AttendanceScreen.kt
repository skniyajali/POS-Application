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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor6
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.IconBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithBorderCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditAbsentScreenDestination
import com.niyaj.popos.features.destinations.SalaryScreenDestination
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.utils.toDate
import com.niyaj.popos.utils.toMonthAndYear
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Attendance Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param attendanceViewModel
 * @param resultRecipient
 * @see AddEditAbsentScreenDestination
 * @see AttendanceViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
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

    val groupedEmployeeAbsent = attendances.groupBy { it.employee?.employeeId }

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
    val searchText = attendanceViewModel.searchText.collectAsStateWithLifecycle().value

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

    SentryTraced(tag = "AttendanceScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedAttendance.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar) {
                    attendanceViewModel.onSearchBarCloseAndClearClick()
                } else {
                    navController.navigateUp()
                }
            },
            title = {
                if (selectedAttendance.isEmpty()) {
                    Text(text = "Absent Reports")
                }
            },
            isFloatingActionButtonDocked = attendances.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
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
                AttendanceNavActions(
                    attendancesSizeIsEmpty = attendances.isEmpty(),
                    selectedAttendance = selectedAttendance,
                    onClickEdit = {
                        navController.navigate(
                            AddEditAbsentScreenDestination(
                                attendanceId = selectedAttendance,
                                employeeId = selectedEmployee
                            )
                        )
                    },
                    onClickDelete = {
                        dialogState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        attendanceViewModel.onEvent(AttendanceEvent.OnSearchAttendance(it))
                    },
                    onClearClick = {
                        attendanceViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        attendanceViewModel.onEvent(AttendanceEvent.ToggleSearchBar)
                    },
                    onClickSalaryBtn = {
                        navController.navigate(SalaryScreenDestination)
                    }
                )
            },
            navigationIcon = {
                if(selectedAttendance.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            attendanceViewModel.onEvent(AttendanceEvent.SelectAttendance(selectedAttendance))
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
                            attendanceViewModel.onEvent(
                                AttendanceEvent.SelectAttendance(selectedAttendance)
                            )
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
                if (attendances.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError
                            ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(
                                id = R.string.no_items_in_absent),
                        buttonText = stringResource(id = R.string.create_absent_entry).uppercase(),
                        onClick = {
                            navController.navigate(AddEditAbsentScreenDestination())
                        }
                    )
                } else {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                    ) {
                        item(key = "employeeAbsents") {
                            groupedEmployeeAbsent.forEach { (employeeId, employeeAttendances) ->
                                employeeId?.let { empId ->
                                    val data = attendanceViewModel.getEmployeeById(empId)
                                    data?.let { employee ->
                                        AbsentEmployees(
                                            employee = employee,
                                            groupedAttendances = employeeAttendances.groupBy { toMonthAndYear(it.absentDate) },
                                            isExpanded = selectedEmployee == empId,
                                            selectedAttendance = selectedAttendance,
                                            onClickAttendance = {
                                                attendanceViewModel.onEvent(
                                                    AttendanceEvent.SelectAttendance(it)
                                                )
                                            },
                                            onSelectEmployee = {
                                                attendanceViewModel.onEvent(
                                                    AttendanceEvent.SelectEmployee(it)
                                                )
                                            } ,
                                            onExpandChange = {
                                                attendanceViewModel.onEvent(
                                                    AttendanceEvent.SelectEmployee(it)
                                                )
                                            },
                                            onAbsentEntry = {
                                                navController.navigate(
                                                    AddEditAbsentScreenDestination(employeeId = it)
                                                )
                                            },
                                        )
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
}

/**
 *
 */
@Composable
fun AttendanceNavActions(
    attendancesSizeIsEmpty: Boolean = true,
    selectedAttendance: String = "",
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    showSearchBar: Boolean = false,
    searchText: String = "",
    onSearchTextChanged: (searchText: String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onClickSearch: () -> Unit = {},
    onClickSalaryBtn: () -> Unit = {},
) {
    ScaffoldNavActions(
        multiSelect = false,
        allItemsIsEmpty = attendancesSizeIsEmpty,
        selectedItem = selectedAttendance,
        onClickEdit = onClickEdit,
        onClickDelete = onClickDelete,
        showSearchBar = showSearchBar,
        searchText = searchText,
        onSearchTextChanged = onSearchTextChanged,
        onClearClick = onClearClick,
        onClickSearch = onClickSearch,
        content = {
            IconButton(
                onClick = onClickSalaryBtn
            ) {
                Icon(imageVector = Icons.Default.Money, contentDescription = "Open Salary Screen")
            }
        }
    )
}


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AbsentEmployees(
    employee: Employee,
    isExpanded: Boolean,
    groupedAttendances:  Map<String, List<EmployeeAttendance>>,
    selectedAttendance : String = "",
    onClickAttendance: (attendanceId: String) -> Unit,
    onSelectEmployee: (employeeId: String) -> Unit,
    onExpandChange: (employeeId: String) -> Unit,
    onAbsentEntry: (employeeId: String) -> Unit
) {
    Card(
        onClick = {
            onSelectEmployee(employee.employeeId)
        },
        modifier = Modifier
            .testTag(employee.employeeName.plus("Tag"))
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = isExpanded,
            onExpandChanged = {
                onExpandChange(employee.employeeId)
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
                        onAbsentEntry(employee.employeeId)
                    }
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = {
                        onSelectEmployee(employee.employeeId)
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
                EmployeeAbsentData(
                    groupedAttendances = groupedAttendances,
                    selectedAttendance = selectedAttendance,
                    onClickAttendance = onClickAttendance,
                )
            }
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EmployeeAbsentData(
    groupedAttendances:  Map<String, List<EmployeeAttendance>>,
    selectedAttendance : String = "",
    onClickAttendance: (attendanceId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        Spacer(modifier = Modifier.height(SpaceMini))

        groupedAttendances.forEach { grouped ->
            TextWithBorderCount(
                modifier = Modifier,
                text = grouped.key,
                leadingIcon = Icons.Default.CalendarMonth,
                count = grouped.value.size,
            )

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceMini),
                crossAxisSpacing = SpaceMini,
            ) {
                grouped.value.forEach{  attendance ->
                    Card(
                        modifier = Modifier
                            .testTag(attendance.employee?.employeeName.plus(attendance.absentDate.toDate)),
                        onClick = {
                            onClickAttendance(attendance.attendeeId)
                        },
                        backgroundColor = LightColor6,
                        elevation = 2.dp,
                        border = if (selectedAttendance == attendance.attendeeId) BorderStroke(1.dp, MaterialTheme.colors.primary) else null,
                    ) {
                        Text(
                            text = attendance.absentDate.toDate,
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colors.secondaryVariant,
                            modifier = Modifier
                                .padding(SpaceSmall)
                        )
                    }

                    Spacer(modifier = Modifier.width(SpaceSmall))
                }
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
    }
}