package com.niyaj.popos.features.employee.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.destinations.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import de.charlex.compose.RevealSwipe
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun EmployeeScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    employeeViewModel: EmployeeViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditEmployeeScreenDestination, String>,
    addEditSalaryRecipient: ResultRecipient<AddEditSalaryScreenDestination, String>,
    addEditAbsentRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val employees by lazy { employeeViewModel.state.employees }
    val isLoading = employeeViewModel.state.isLoading
    val hasError by lazy { employeeViewModel.state.error }

    val selectedEmployeeItem = employeeViewModel.selectedEmployee.collectAsState().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedEmployeeItem.isNotEmpty(), label = "isContextual")

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

    val showSearchBar = employeeViewModel.toggledSearchBar.collectAsState().value

    LaunchedEffect(key1 = true) {
        employeeViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    Timber.d(event.successMessage)

                }

                is UiEvent.OnError -> {
                    Timber.d(event.errorMessage)
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
            employeeViewModel.onSearchBarCloseAndClearClick()
        } else if (selectedEmployeeItem.isNotEmpty()) {
            employeeViewModel.onEmployeeEvent(
                EmployeeEvent.SelectEmployee(selectedEmployeeItem)
            )
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
                }
            }
            is NavResult.Value -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    addEditSalaryRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
                }
            }
            is NavResult.Value -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    addEditAbsentRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
                }
            }
            is NavResult.Value -> {
                if (selectedEmployeeItem.isNotEmpty()) {
                    employeeViewModel.onEmployeeEvent(
                        EmployeeEvent.SelectEmployee(selectedEmployeeItem)
                    )
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
                employeeViewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigateUp()
            }
        },
        title = {
            Text(text = "Employees")
        },
        isFloatingActionButtonDocked = employees.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_employee).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = employees.isNotEmpty() && selectedEmployeeItem.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditEmployeeScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if (showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = employeeViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for employees...",
                    onSearchTextChanged = {
                        employeeViewModel.onEmployeeEvent(EmployeeEvent.OnSearchEmployee(it))
                    },
                    onClearClick = {
                        employeeViewModel.onSearchTextClearClick()
                    },
                )
            } else {
                if (employees.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            employeeViewModel.onEmployeeEvent(EmployeeEvent.ToggleSearchBar)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search_icon),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }

                    IconButton(
                        onClick = {
                            onOpenSheet(
                                BottomSheetScreen.FilterEmployeeScreen(
                                    filterEmployee = employeeViewModel.state.filterEmployee,
                                    onFilterChanged = {
                                        employeeViewModel.onEmployeeEvent(
                                            EmployeeEvent.OnFilterEmployee(
                                                it
                                            )
                                        )
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_employee),
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

                IconButton(
                    onClick = {
                        navController.navigate(AttendanceScreenDestination())
                    }
                ) {
                    Icon(imageVector = Icons.Default.EventBusy, contentDescription = "Add Absent Entry")
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
                        employeeViewModel.onEmployeeEvent(
                            EmployeeEvent.DeleteEmployee(
                                selectedEmployeeItem
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
            title(text = "Delete Employee?")
            message(res = R.string.delete_employee_msg)
        }

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                employeeViewModel.onEmployeeEvent(EmployeeEvent.RefreshEmployee)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
            ) {
                if (employees.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError
                            ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(
                                id = R.string.no_items_in_employee),
                        buttonText = stringResource(id = R.string.create_new_employee).uppercase(),
                        onClick = {
                            navController.navigate(AddEditEmployeeScreenDestination())
                        }
                    )
                } else {
                    LazyColumn(
                        state = lazyListState,
                    ) {
                        itemsIndexed(employees) { index, employee ->
                            RevealSwipe(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onContentClick = {
                                    navController.navigate(
                                        EmployeeDetailsScreenDestination(employeeId = employee.employeeId)
                                    )
                                },
                                closeOnBackgroundClick = true,
                                maxRevealDp = 150.dp,
                                hiddenContentStart = {
                                    IconButton(
                                        onClick = {
                                            navController.navigate(AddEditSalaryScreenDestination(
                                                employeeId = employee.employeeId))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            navController.navigate(AddEditEmployeeScreenDestination(
                                                employeeId = employee.employeeId))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }
                                },
                                hiddenContentEnd = {
                                    IconButton(
                                        onClick = {
                                            navController.navigate(AddEditAbsentScreenDestination(employeeId = employee.employeeId))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EventBusy,
                                            contentDescription = "Mark as Absent",
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }

                                    IconButton(onClick = {
                                        dialogState.show()
                                        employeeViewModel.onEmployeeEvent(EmployeeEvent.SelectEmployee(employee.employeeId))
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete order",
                                            modifier = Modifier.padding(horizontal = 25.dp),
                                        )
                                    }
                                },
                                contentColor = MaterialTheme.colors.primary,
                                backgroundCardContentColor = LightColor12,
                                backgroundCardStartColor = MaterialTheme.colors.primary,
                                backgroundCardEndColor = MaterialTheme.colors.error,
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shape = it,
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.SpaceBetween,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(
                                                text = employee.employeeName,
                                                style = MaterialTheme.typography.body1,
                                                textAlign = TextAlign.Center,
                                                fontWeight = FontWeight.Bold,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            Spacer(modifier = Modifier.height(SpaceMini))

                                            Text(
                                                text = employee.employeePhone,
                                                style = MaterialTheme.typography.body1,
                                                textAlign = TextAlign.Center,
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    navController.navigate(
                                                        EmployeeDetailsScreenDestination(employeeId = employee.employeeId)
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.OpenInNew,
                                                    contentDescription = stringResource(id = R.string.order_details),
                                                    tint = MaterialTheme.colors.primary,
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                            if (index == employees.size - 1) {
                                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                            }
                        }
                    }
                }
            }
        }
    }
}