package com.niyaj.popos.features.expenses.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.toPrettyDate
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.NoteText
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.destinations.AddEditExpensesScreenDestination
import com.niyaj.popos.features.destinations.ExpensesSettingScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.employee.domain.util.EmployeeTestTags
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber
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
@OptIn(ExperimentalComposeUiApi::class)
@Destination
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

    val groupedExpenses =
        viewModel.state.collectAsStateWithLifecycle().value.expenses.groupBy { it.createdAt.toPrettyDate() }
    val selectedExpensesItem = viewModel.selectedExpenses.collectAsStateWithLifecycle().value
    val isLoading = viewModel.state.collectAsStateWithLifecycle().value.isLoading
    val error = viewModel.state.collectAsStateWithLifecycle().value.error

    val totalAmount = viewModel.totalState.collectAsStateWithLifecycle().value.totalAmount
    val totalPayment = viewModel.totalState.collectAsStateWithLifecycle().value.totalPayment
    val selectedDate = viewModel.totalState.collectAsStateWithLifecycle().value.selectedDate

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedExpensesItem.isNotEmpty(), label = "isContextual")

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

    val showSearchBar = viewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.collectAsStateWithLifecycle().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

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

                is UiEvent.IsLoading -> {
                    Timber.d("Loading.. ${event.isLoading.toString()}")
                }
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedExpensesItem.isNotEmpty()) {
                    viewModel.onExpensesEvent(
                        ExpensesEvent.SelectExpenses(
                            selectedExpensesItem
                        )
                    )
                }
            }

            is NavResult.Value -> {
                if (selectedExpensesItem.isNotEmpty()) {
                    viewModel.onExpensesEvent(
                        ExpensesEvent.SelectExpenses(
                            selectedExpensesItem
                        )
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

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.onSearchBarCloseAndClearClick()
        } else if (selectedExpensesItem.isNotEmpty()) {
            viewModel.onExpensesEvent(ExpensesEvent.SelectExpenses(selectedExpensesItem))
        } else {
            navController.navigateUp()
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

    SentryTraced(tag = "ExpensesScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedExpensesItem.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar) {
                    viewModel.onSearchBarCloseAndClearClick()
                } else {
                    navController.navigate(MainFeedScreenDestination())
//                navController.navigate(CustomerS)
                }
            },
            title = {
                if (selectedExpensesItem.isEmpty()) {
                    Text(text = "Expenses")
                }
            },
            isFloatingActionButtonDocked = groupedExpenses.isNotEmpty(),
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.add_new_expense).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = groupedExpenses.isNotEmpty() && selectedExpensesItem.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditExpensesScreenDestination())
                    },
                )
            },
            navActions = {
                ScaffoldNavActions(
                    multiSelect = false,
                    allItemsIsEmpty = groupedExpenses.isEmpty(),
                    selectedItem = selectedExpensesItem,
                    onClickEdit = {
                        navController.navigate(AddEditExpensesScreenDestination(expensesId = selectedExpensesItem))
                    },
                    onClickDelete = {
                        dialogState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        viewModel.onExpensesEvent(ExpensesEvent.OnSearchExpenses(it))
                    },
                    onClearClick = {
                        viewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        viewModel.onExpensesEvent(ExpensesEvent.ToggleSearchBar)
                    },
                    showSettingsIcon = true,
                    onClickSetting = {
                        navController.navigate(ExpensesSettingScreenDestination())
                    },
                )
            },
            navigationIcon = {
                if (selectedExpensesItem.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.onExpensesEvent(
                                ExpensesEvent.SelectExpenses(
                                    selectedExpensesItem
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.filter_product),
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
                            viewModel.onExpensesEvent(
                                ExpensesEvent.DeleteExpenses(selectedExpensesItem)
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
                title(text = "Delete Expenses?")
                message(res = R.string.delete_expenses_msg)
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
                    viewModel.onExpensesEvent(ExpensesEvent.OnSelectDate(date.toString()))
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    viewModel.onExpensesEvent(ExpensesEvent.RefreshExpenses)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(SpaceSmall),
                ) {
                    TotalExpenses(
                        selectedDate = selectedDate,
                        totalAmount = totalAmount,
                        totalPayment = totalPayment,
                        onClickDatePicker = { datePickerState.show() }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    if (groupedExpenses.isEmpty() || error != null) {
                        ItemNotAvailable(
                            text = error ?: if (showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(
                                id = R.string.no_item_in_expenses
                            ),
                            buttonText = stringResource(id = R.string.add_new_expense).uppercase(),
                            onClick = {
                                navController.navigate(AddEditExpensesScreenDestination())
                            }
                        )
                    } else if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        ExpensesItems(
                            lazyListState = lazyListState,
                            showScrollToTop = showScrollToTop.value,
                            groupedExpenses = groupedExpenses,
                            doesSelected = {
                                selectedExpensesItem == it
                            },
                            doesAnySelected = selectedExpensesItem.isNotEmpty(),
                            onSelectExpense = {
                                viewModel.onExpensesEvent(ExpensesEvent.SelectExpenses(it))
                            }
                        )
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
fun TotalExpenses(
    selectedDate : String,
    totalAmount : String,
    totalPayment : Int,
    onClickDatePicker : () -> Unit
) {
    Card(
        modifier = Modifier
            .testTag("CalculateTotalExpenses")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceMedium),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total Expenses",
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )

                RoundedBox(
                    text = selectedDate,
                    showIcon = true,
                    onClick = onClickDatePicker,
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
            Spacer(modifier = Modifier.height(SpaceSmall))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceSmall))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = totalAmount.toRupee,
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag(EmployeeTestTags.REMAINING_AMOUNT_TEXT)
                )

                Text(
                    text = "Total $totalPayment Payment",
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.End
                )
            }

            if (totalPayment == 0) {
                Spacer(modifier = Modifier.height(SpaceMini))
                NoteText(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "You haven't paid any expenses yet."
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesItems(
    lazyListState: LazyListState,
    showScrollToTop: Boolean,
    groupedExpenses: Map<String, List<Expenses>>,
    doesSelected : (String) -> Boolean,
    doesAnySelected : Boolean,
    onSelectExpense : (String) -> Unit,
    headerColor: Color = MaterialTheme.colors.onPrimary,
) {
    LazyColumn(
        state = lazyListState,
        modifier = Modifier,
    ) {
        groupedExpenses.forEach { (date, expensesList) ->
            stickyHeader {
                TextWithCount(
                    modifier = Modifier
                        .background(if (showScrollToTop) headerColor else Color.Transparent)
                        .clip(RoundedCornerShape(if (showScrollToTop) 4.dp else 0.dp)),
                    text = date,
                    count = expensesList.count(),
                    onClick = {}
                )
            }

            itemsIndexed(
                items = expensesList,
                key = { index, item ->
                    item.expensesId.plus(index)
                }
            ) { index, expense ->
                ExpensesItem(
                    expense = expense,
                    doesAnySelected = doesAnySelected,
                    doesSelected = doesSelected,
                    onSelectExpense = onSelectExpense,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                if (index == expensesList.size - 1) {
                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}


/**
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpensesItem(
    expense: Expenses,
    doesSelected: (String) -> Boolean,
    doesAnySelected: Boolean = true,
    onSelectExpense: (String) -> Unit,
    onClickExpense: (String) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (!doesAnySelected) {
                        onClickExpense(expense.expensesId)
                    } else {
                        onSelectExpense(expense.expensesId)
                    }
                },
                onLongClick = {
                    onSelectExpense(expense.expensesId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.onSurface,
        border = if (doesSelected(expense.expensesId))
            BorderStroke(1.dp, MaterialTheme.colors.primary)
        else null,
        elevation = 2.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = SpaceSmall)
                .padding(SpaceSmall)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = expense.expensesCategory?.expensesCategoryName!!,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                if (expense.expensesRemarks.isNotEmpty()) {
                    NoteText(text = expense.expensesRemarks)
                }
                Text(
                    text = expense.expensesPrice.toRupee,
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
