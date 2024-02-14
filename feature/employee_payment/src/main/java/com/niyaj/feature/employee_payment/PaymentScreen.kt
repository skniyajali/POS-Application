package com.niyaj.feature.employee_payment

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.PaymentScreenTags
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_MESSAGE
import com.niyaj.common.tags.PaymentScreenTags.DELETE_PAYMENT_TITLE
import com.niyaj.common.tags.PaymentScreenTags.NO_ITEMS_IN_PAYMENT
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_NOT_AVAILABLE
import com.niyaj.common.tags.PaymentScreenTags.PAYMENT_SCREEN_TITLE
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.employee_payment.components.EmployeePayments
import com.niyaj.feature.employee_payment.components.TotalPayment
import com.niyaj.feature.employee_payment.destinations.AddEditPaymentScreenDestination
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
 * Salary Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @see PaymentViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.PAYMENT_SCREEN)
@Composable
fun PaymentScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: PaymentViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditPaymentScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val uiState = viewModel.payments.collectAsStateWithLifecycle().value

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
                viewModel.deselectItems()
            }
            is NavResult.Value -> {
                viewModel.deselectItems()
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        title = if (selectedItems.isEmpty()) PAYMENT_SCREEN_TITLE else "${selectedItems.size} Selected",
        showBackButton = selectedItems.isEmpty(),
        selectionCount = selectedItems.size,
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = PaymentScreenTags.CREATE_NEW_PAYMENT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditPaymentScreenDestination())
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
                placeholderText = PaymentScreenTags.PAYMENT_SEARCH_PLACEHOLDER,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditPaymentScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    dialogState.show()
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Crossfade(
            targetState = uiState,
            label = "Payment::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) PAYMENT_NOT_AVAILABLE else NO_ITEMS_IN_PAYMENT,
                        buttonText = PaymentScreenTags.CREATE_NEW_PAYMENT,
                        onClick = {
                            navController.navigate(AddEditPaymentScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    val groupedByEmployeeSalaries = remember(state.data) {
                        state.data.groupBy { it.employee }
                    }

                    val totalAmount = state.data.sumOf { it.paymentAmount.toLong() }.toString()
                    val employeeCount = groupedByEmployeeSalaries.keys.size
                    val paymentsCount = state.data.size

                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.padding(SpaceSmall)
                    ) {
                        item(key = "totalPayments") {
                            TotalPayment(
                                totalAmount = totalAmount,
                                paymentsCount = paymentsCount,
                                employeesCount = employeeCount,
                                onClickEmployeeCount = {
                                    scope.launch {
                                        lazyListState.animateScrollToItem(1)
                                    }
                                },
                                onClickTotalPayments = {
                                    scope.launch {
                                        lazyListState.animateScrollToItem(1)
                                    }
                                },
                                onClickAbsentEntry = {
                                    navController.navigate(Screens.ATTENDANCE_SCREEN)
                                }
                            )
                        }

                        item(key = "employeePayments") {
                            groupedByEmployeeSalaries.forEach { (employee, employeeSalaries) ->
                                employee?.let { emp ->
                                    EmployeePayments(
                                        employee = employee,
                                        employeeSalaries = employeeSalaries,
                                        isExpanded = selectedEmployee == emp.employeeId,
                                        onSelectEmployee = viewModel::selectEmployee,
                                        onExpandChanged = viewModel::selectEmployee,
                                        onClickAddSalaryBtn = {
                                            navController.navigate(
                                                AddEditPaymentScreenDestination(employeeId = it)
                                            )
                                        },
                                        doesSelected = { selectedItems.contains(it) },
                                        onClick = {
                                            if (selectedItems.isNotEmpty()) {
                                                viewModel.selectItem(it)
                                            }
                                        },
                                        onLongClick = viewModel::selectItem,
                                    )

                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                }
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
        title(text = DELETE_PAYMENT_TITLE)
        message(text = DELETE_PAYMENT_MESSAGE)
    }
}