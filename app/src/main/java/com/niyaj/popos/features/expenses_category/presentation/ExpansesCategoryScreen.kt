package com.niyaj.popos.features.expenses_category.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.FlexRowBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditExpensesCategoryScreenDestination
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
 *  Expenses Category Screen
 *  @author Sk Niyaj Ali
 *  @param navController
 *  @param scaffoldState
 *  @param viewModel
 *  @param resultRecipient
 *  @see ExpensesCategoryViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ExpensesCategoryScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: ExpensesCategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpensesCategoryScreenDestination, String>
) {
    val lazyGridState = rememberLazyGridState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val expensesCategories = viewModel.expensesCategories.collectAsStateWithLifecycle().value.expensesCategory
    val isLoading: Boolean = viewModel.expensesCategories.collectAsStateWithLifecycle().value.isLoading
    val hasError = viewModel.expensesCategories.collectAsStateWithLifecycle().value.error

    val selectedItem = viewModel.selectedExpensesCategory.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = viewModel.toggledSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    Timber.d(event.successMessage)
                    scaffoldState.snackbarHostState.showSnackbar(event.successMessage)
                }

                is UiEvent.Error -> {
                    Timber.d(event.errorMessage)
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
            lazyGridState.firstVisibleItemIndex > 0
        }
    }
    
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedItem.isNotEmpty()){
                    viewModel.onExpensesCategoryEvent(
                        ExpensesCategoryEvent.SelectExpensesCategory(
                            selectedItem
                        )
                    )
                }
            }
            is NavResult.Value -> {
                if(selectedItem.isNotEmpty()){
                    viewModel.onExpensesCategoryEvent(
                        ExpensesCategoryEvent.SelectExpensesCategory(
                            selectedItem
                        )
                    )
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar){
            viewModel.onSearchBarCloseAndClearClick()
        } else if(selectedItem.isNotEmpty()) {
            viewModel.onExpensesCategoryEvent(
                ExpensesCategoryEvent.SelectExpensesCategory(
                    selectedItem
                )
            )
        }else{
            navController.navigateUp()
        }
    }

    SentryTraced(tag = "ExpensesCategoryScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedItem.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    viewModel.onSearchBarCloseAndClearClick()
                }else{
                    navController.navigateUp()
                }
            },
            title = {
                if (selectedItem.isEmpty()) {
                    Text(text = "Expenses Category")
                }
            },
            isFloatingActionButtonDocked = expensesCategories.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_new_expenses_category).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = expensesCategories.isNotEmpty() && selectedItem.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyGridState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditExpensesCategoryScreenDestination())
                    },
                )
            },
            navActions = {
                ScaffoldNavActions(
                    multiSelect = false,
                    allItemsIsEmpty = expensesCategories.isEmpty(),
                    selectedItem = selectedItem,
                    onClickEdit = {
                        navController.navigate(
                            AddEditExpensesCategoryScreenDestination(selectedItem)
                        )
                    },
                    onClickDelete = {
                        dialogState.show()
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        viewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.OnSearchExpensesCategory(it)
                        )
                    },
                    onClearClick = {
                        viewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        viewModel.onExpensesCategoryEvent(ExpensesCategoryEvent.ToggleSearchBar)
                    }
                )
            },
            navigationIcon = {
                if(selectedItem.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.onExpensesCategoryEvent(
                                ExpensesCategoryEvent.SelectExpensesCategory(
                                    selectedItem
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
                            viewModel.onExpensesCategoryEvent(
                                ExpensesCategoryEvent.DeleteExpensesCategory(
                                    selectedItem
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
                title(text = "Delete Expenses Category?")
                message(res = R.string.delete_expenses_category_msg)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    viewModel.onExpensesCategoryEvent(ExpensesCategoryEvent.RefreshExpenses)
                }
            ) {
                if (expensesCategories.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_expenses_category),
                        buttonText = stringResource(id = R.string.create_new_expenses_category).uppercase(),
                        onClick = {
                            navController.navigate(AddEditExpensesCategoryScreenDestination())
                        }
                    )
                } else if(isLoading){
                    LoadingIndicator()
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                    ){
                        items(
                            items = expensesCategories,
                            key = { it.expensesCategoryId }
                        ){category ->
                            FlexRowBox(
                                title = category.expensesCategoryName,
                                modifier = Modifier,
                                secondaryText = null,
                                icon = Icons.Default.Category,
                                doesSelected = selectedItem == category.expensesCategoryId,
                                doesAnySelected = selectedItem.isNotEmpty(),
                                onSelectItem = {
                                    viewModel.onExpensesCategoryEvent(
                                        ExpensesCategoryEvent.SelectExpensesCategory(
                                            category.expensesCategoryId
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}