package com.niyaj.popos.realm.expenses.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.destinations.AddEditExpensesScreenDestination
import com.niyaj.popos.destinations.ExpensesSettingScreenDestination
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.util.Screen
import com.niyaj.popos.util.toFormattedDate
import com.niyaj.popos.util.toRupee
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

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun ExpensesScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    expensesViewModel: ExpensesViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpensesScreenDestination, String>,
    settingRecipient: ResultRecipient<ExpensesSettingScreenDestination, String>,
) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dialogState = rememberMaterialDialogState()

    val expenses by lazy { expensesViewModel.state.expenses.groupBy { it.createdAt.toFormattedDate } }
    val selectedExpensesItem = expensesViewModel.selectedExpenses.collectAsState().value
    val isLoading: Boolean = expensesViewModel.state.isLoading

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

    val showSearchBar = expensesViewModel.toggledSearchBar.collectAsState().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        expensesViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.OnError -> {
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
        when(result) {
            is NavResult.Canceled -> {
                if(selectedExpensesItem.isNotEmpty()){
                    expensesViewModel.onExpensesEvent(
                        ExpensesEvent.SelectExpenses(
                            selectedExpensesItem
                        )
                    )
                }
            }
            is NavResult.Value -> {
                if(selectedExpensesItem.isNotEmpty()){
                    expensesViewModel.onExpensesEvent(
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
        if (showSearchBar){
            expensesViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedExpensesItem.isNotEmpty()) {
            expensesViewModel.onExpensesEvent(ExpensesEvent.SelectExpenses(selectedExpensesItem))
        }else{
            navController.navigateUp()
        }
    }

    settingRecipient.onNavResult { result ->
        when(result){
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedExpensesItem.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar) {
                expensesViewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigate(Screen.MainFeedScreen.route)
            }
        },
        title = {
            if (selectedExpensesItem.isEmpty()) {
                Text(text = "All Expenses")
            }
        },
        isFloatingActionButtonDocked = expenses.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.add_new_expense).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = expenses.isNotEmpty() && selectedExpensesItem.isEmpty() && !showSearchBar,
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
            if (selectedExpensesItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditExpensesScreenDestination(expensesId = selectedExpensesItem))
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Expenses Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                    enabled = selectedExpensesItem.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Expenses",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            } else if (showSearchBar) {
                StandardSearchBar(
                    searchText = expensesViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for expenses...",
                    onSearchTextChanged = {
                        expensesViewModel.onExpensesEvent(ExpensesEvent.OnSearchExpenses(it))
                    },
                    onClearClick = {
                        expensesViewModel.onSearchTextClearClick()
                    },
                )
            } else {
                if (expenses.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            expensesViewModel.onExpensesEvent(ExpensesEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterExpensesScreen(
                                    filterExpenses = expensesViewModel.state.filterExpenses,
                                    onFilterChanged = {
                                        expensesViewModel.onExpensesEvent(
                                            ExpensesEvent.OnFilterExpenses(it)
                                        )
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_charges_item),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate(ExpensesSettingScreenDestination())
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Open Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
        },
        navigationIcon = {
            if (selectedExpensesItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        expensesViewModel.onExpensesEvent(
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
                        expensesViewModel.onExpensesEvent(
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

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = isLoading),
            onRefresh = {
                expensesViewModel.onExpensesEvent(ExpensesEvent.RefreshExpenses)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall),
            ) {
                if (expenses.isEmpty()) {
                    ItemNotAvailable(
                        text = if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_item_in_expenses),
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
                    LazyColumn(
                        state = lazyListState
                    ) {
                        expenses.forEach { (date, expensesList) ->
                            stickyHeader {
                                TextWithCount(
                                    modifier = Modifier
                                        .background(
                                            if (showScrollToTop.value)
                                                MaterialTheme.colors.onPrimary
                                            else Color.Transparent
                                        )
                                        .clip(
                                            RoundedCornerShape(if (showScrollToTop.value) 4.dp else 0.dp)
                                        ),
                                    text = if(date == System.currentTimeMillis().toString().toFormattedDate) "Today" else date,
                                    count = expensesList.count(),
                                    onClick = {

                                    }
                                )
                            }

                            itemsIndexed(expensesList) { index, expense ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            expensesViewModel.onExpensesEvent(
                                                ExpensesEvent.SelectExpenses(expense.expensesId)
                                            )
                                        },
                                    shape = RoundedCornerShape(4.dp),
                                    backgroundColor = MaterialTheme.colors.onPrimary,
                                    contentColor = MaterialTheme.colors.onSurface,
                                    border = if (selectedExpensesItem == expense.expensesId)
                                        BorderStroke(1.dp, MaterialTheme.colors.primary)
                                    else null,
                                    elevation = 2.dp,
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(SpaceSmall)
                                            .fillMaxWidth(),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(
                                                verticalArrangement = Arrangement.SpaceBetween,
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text(
                                                    text = expense.expensesCategory?.expensesCategoryName!!,
                                                    style = MaterialTheme.typography.body1,
                                                    fontWeight = FontWeight.SemiBold,
                                                    textAlign = TextAlign.Center,
                                                )
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                                Text(
                                                    text = expense.expensesPrice.toRupee,
                                                    style = MaterialTheme.typography.body1,
                                                    textAlign = TextAlign.Center,
                                                )
                                            }
                                        }

                                        if(expense.expensesRemarks.isNotEmpty()){
                                            Spacer(modifier = Modifier.height(SpaceSmall))
                                            Divider(
                                                modifier = Modifier.fillMaxWidth(),
                                            )
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                            Text(
                                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                                text = expense.expensesRemarks,
                                                style = MaterialTheme.typography.body1,
                                                textAlign = TextAlign.Center,
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                if (index == expensesList.size - 1) {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}