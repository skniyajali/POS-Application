package com.niyaj.feature.employee_attendance

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_NOT_AVAILABLE
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_SCREEN_TITLE
import com.niyaj.common.tags.AbsentScreenTestTags.ABSENT_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.AbsentScreenTestTags.CREATE_NEW_ABSENT
import com.niyaj.common.tags.AbsentScreenTestTags.DELETE_ABSENT_MESSAGE
import com.niyaj.common.tags.AbsentScreenTestTags.DELETE_ABSENT_TITLE
import com.niyaj.common.tags.AbsentScreenTestTags.NO_ITEMS_IN_ABSENT
import com.niyaj.common.utils.toMonthAndYear
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.employee_attendance.components.AbsentEmployees
import com.niyaj.feature.employee_attendance.destinations.AddEditAbsentScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFAB
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

/**
 * Attendance Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @see AddEditAbsentScreenDestination
 * @see AttendanceViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.ATTENDANCE_SCREEN)
@Composable
fun AttendanceScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: AttendanceViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditAbsentScreenDestination, String>,
) {

    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val uiState = viewModel.absents.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty()

    val selectedItems = viewModel.selectedItems.toList()
    val selectedEmployee = viewModel.selectedEmployee.collectAsStateWithLifecycle().value

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.errorMessage)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedItems.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedItems.isNotEmpty()) {
                    viewModel.deselectItems()
                }
            }

            is NavResult.Value -> {
                if (selectedItems.isNotEmpty()) {
                    viewModel.deselectItems()
                }
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        selectionCount = selectedItems.size,
        showBackButton = selectedItems.isEmpty(),
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        title = if (selectedItems.isEmpty()) ABSENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_ABSENT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditAbsentScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        navActions = {
            ScaffoldNavActions(
                placeholderText = ABSENT_SEARCH_PLACEHOLDER,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditAbsentScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    dialogState.show()
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Crossfade(
            targetState = uiState,
            label = "Absent State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) ABSENT_NOT_AVAILABLE else NO_ITEMS_IN_ABSENT,
                        buttonText = CREATE_NEW_ABSENT,
                        onClick = {
                            navController.navigate(AddEditAbsentScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall),
                    ) {
                        item(key = "employeeAbsents") {
                            val groupedEmployeeAbsent = remember(state.data) {
                                state.data.groupBy { it.employee }
                            }

                            groupedEmployeeAbsent.forEach { (employee, employeeAttendances) ->
                                employee?.let { emp ->
                                    AbsentEmployees(
                                        employee = employee,
                                        groupedAttendances = employeeAttendances.groupBy {
                                            toMonthAndYear(
                                                it.absentDate
                                            )
                                        },
                                        isExpanded = selectedEmployee == emp.employeeId,
                                        doesSelected = { selectedItems.contains(it) },
                                        onClick = {
                                            if (selectedItems.isNotEmpty()){
                                                viewModel.selectItem(it)
                                            }
                                        },
                                        onLongClick = viewModel::selectItem,
                                        onSelectEmployee = viewModel::selectEmployee,
                                        onExpandChange = viewModel::selectEmployee,
                                        onAbsentEntry = {
                                            navController.navigate(
                                                AddEditAbsentScreenDestination(employeeId = it)
                                            )
                                        },
                                    )
                                }

                                Spacer(modifier = Modifier.height(SpaceSmall))
                            }
                        }
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = dialogState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    dialogState.hide()
                    viewModel.deselectItems()
                },
            )
        }
    ) {
        title(text = DELETE_ABSENT_TITLE)
        message(text = DELETE_ABSENT_MESSAGE)
    }
}