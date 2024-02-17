package com.niyaj.feature.expenses

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.niyaj.common.tags.ExpenseTestTags
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_NOT_AVAILABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_SCREEN_TITLE
import com.niyaj.common.tags.ExpenseTestTags.NO_ITEMS_IN_EXPENSE
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.expenses.components.ExpensesItem
import com.niyaj.feature.expenses.components.GroupedExpensesData
import com.niyaj.feature.expenses.components.TotalExpenses
import com.niyaj.feature.expenses.destinations.AddEditExpensesScreenDestination
import com.niyaj.feature.expenses.destinations.ExpensesSettingScreenDestination
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
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Expenses Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @param settingRecipient
 * @see ExpensesViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.EXPENSES_SCREEN)
@Composable
fun ExpensesScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: ExpensesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpensesScreenDestination, String>,
    settingRecipient: ResultRecipient<ExpensesSettingScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()
    val datePickerState = rememberMaterialDialogState()

    val uiState = viewModel.expenses.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty()

    val selectedItems = viewModel.selectedItems.toList()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val selectedDate = viewModel.selectedDate.collectAsStateWithLifecycle().value.toPrettyDate()
    val totalAmount = viewModel.totalAmount.collectAsStateWithLifecycle().value
    val totalItems = viewModel.totalItems.size

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
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

    settingRecipient.onNavResult { result ->
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
        showBackButton = selectedItems.isEmpty(),
        selectionCount = selectedItems.size,
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        title = if (selectedItems.isEmpty()) EXPENSE_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        fabPosition = if (lazyListState.isScrolled) FabPosition.End else FabPosition.Center,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyListState.isScrolled,
                fabText = CREATE_NEW_EXPENSE,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditExpensesScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                }
            )
        },
        navActions = {
            ScaffoldNavActions(
                placeholderText = ExpenseTestTags.EXPENSE_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditExpensesScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    dialogState.show()
                },
                onSettingsClick = {
                    navController.navigate(ExpensesSettingScreenDestination)
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall),
        ) {
            TotalExpenses(
                totalAmount = totalAmount,
                totalPayment = totalItems,
                selectedDate = selectedDate,
                onClickDatePicker = {
                    datePickerState.show()
                }
            )

            Crossfade(
                targetState = uiState,
                label = "Expenses::State"
            ) { state ->
                when(state) {
                    is UiState.Empty -> {
                        ItemNotAvailable(
                            text = if (searchText.isEmpty()) EXPENSE_NOT_AVAILABLE else NO_ITEMS_IN_EXPENSE,
                            buttonText = CREATE_NEW_EXPENSE,
                            onClick = {
                                navController.navigate(AddEditExpensesScreenDestination())
                            }
                        )
                    }

                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Success -> {
                        val groupedExpenses = remember(state.data) {
                            state.data.groupBy { it.expensesCategory?.expensesCategoryName!! }
                        }

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        LazyColumn(
                            state = lazyListState
                        ) {
                            groupedExpenses.forEach { (categoryName, expenses) ->
                                if (expenses.size > 1) {
                                    item {
                                        GroupedExpensesData(
                                            items = expenses,
                                            categoryName = categoryName,
                                            doesSelected = {
                                                selectedItems.contains(it)
                                            },
                                            onClick = {
                                                if (selectedItems.isNotEmpty()) {
                                                    viewModel.selectItem(it)
                                                }
                                            },
                                            onLongClick = viewModel::selectItem
                                        )
                                    }
                                }else {
                                    item {
                                        ExpensesItem(
                                            categoryName = categoryName,
                                            expense = expenses.first(),
                                            doesSelected = {
                                                selectedItems.contains(it)
                                            },
                                            onClick = {
                                                if (selectedItems.isNotEmpty()) {
                                                    viewModel.selectItem(it)
                                                }
                                            },
                                            onLongClick = viewModel::selectItem
                                        )
                                    }
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
                },
            )
        }
    ) {
        title(text = DELETE_EXPENSE_TITLE)
        message(text = DELETE_EXPENSE_MESSAGE)
    }

    MaterialDialog(
        dialogState = datePickerState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker(
            allowedDateValidator = { date ->
                date <= LocalDate.now()
            }
        ) { date ->
            viewModel.selectDate(date.toMilliSecond)
        }
    }
}
