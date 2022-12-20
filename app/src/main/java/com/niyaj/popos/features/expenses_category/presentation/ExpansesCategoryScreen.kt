package com.niyaj.popos.features.expenses_category.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.destinations.AddEditExpensesCategoryScreenDestination
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

@Destination
@Composable
fun ExpensesCategoryScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    expensesCategoryViewModel: ExpensesCategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditExpensesCategoryScreenDestination, String>
) {
    val lazyListState = rememberLazyGridState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val expensesCategories = expensesCategoryViewModel.expensesCategories.collectAsState().value.expensesCategory
    val isLoading: Boolean = expensesCategoryViewModel.expensesCategories.collectAsState().value.isLoading
    val hasError = expensesCategoryViewModel.expensesCategories.collectAsState().value.error

    val selectedExpensesCategoryItem = expensesCategoryViewModel.selectedExpensesCategory.collectAsState().value
    val filterExpensesCategory = expensesCategoryViewModel.expensesCategories.collectAsState().value.filterExpensesCategory

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedExpensesCategoryItem.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = expensesCategoryViewModel.toggledSearchBar.collectAsState().value

    LaunchedEffect(key1 = true) {
        expensesCategoryViewModel.eventFlow.collect { event ->
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


    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedExpensesCategoryItem.isNotEmpty()){
                    expensesCategoryViewModel.onExpensesCategoryEvent(
                        ExpensesCategoryEvent.SelectExpensesCategory(
                            selectedExpensesCategoryItem
                        )
                    )
                }
            }
            is NavResult.Value -> {
                if(selectedExpensesCategoryItem.isNotEmpty()){
                    expensesCategoryViewModel.onExpensesCategoryEvent(
                        ExpensesCategoryEvent.SelectExpensesCategory(
                            selectedExpensesCategoryItem
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
            expensesCategoryViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedExpensesCategoryItem.isNotEmpty()) {
            expensesCategoryViewModel.onExpensesCategoryEvent(
                ExpensesCategoryEvent.SelectExpensesCategory(
                    selectedExpensesCategoryItem
                )
            )
        }else{
            navController.navigateUp()
        }
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = selectedExpensesCategoryItem.isEmpty(),
        onBackButtonClick = {
            if (showSearchBar){
                expensesCategoryViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        title = {
            if (selectedExpensesCategoryItem.isEmpty()) {
                Text(text = "Expenses Category")
            }
        },
        isFloatingActionButtonDocked = expensesCategories.isNotEmpty(),
        floatingActionButton = {
            ExtendedFabButton(
                text = stringResource(id = R.string.create_new_expenses_category).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = expensesCategories.isNotEmpty() && selectedExpensesCategoryItem.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditExpensesCategoryScreenDestination())
                },
            )
        },
        navActions = {
            if(selectedExpensesCategoryItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        navController.navigate(AddEditExpensesCategoryScreenDestination(expensesCategoryId = selectedExpensesCategoryItem))
                    },
                ){
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit ExpensesCategory Item",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        dialogState.show()
                    },
                    enabled = selectedExpensesCategoryItem.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete ExpensesCategory",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = expensesCategoryViewModel.searchText.collectAsState().value,
                    placeholderText = "Search for employees...",
                    onSearchTextChanged = {
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.OnSearchExpensesCategory(
                                it
                            )
                        )
                    },
                    onClearClick = {
                        expensesCategoryViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (expensesCategories.isNotEmpty()){
                    IconButton(
                        onClick = {
                            expensesCategoryViewModel.onExpensesCategoryEvent(ExpensesCategoryEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterExpensesCategoryScreen(
                                    filterExpensesCategory = filterExpensesCategory,
                                    onFilterChanged = {
                                        expensesCategoryViewModel.onExpensesCategoryEvent(
                                            ExpensesCategoryEvent.OnFilterExpensesCategory(it)
                                        )
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_expenses_category),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if(selectedExpensesCategoryItem.isNotEmpty()) {
                IconButton(
                    onClick = {
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.SelectExpensesCategory(
                                selectedExpensesCategoryItem
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
                        expensesCategoryViewModel.onExpensesCategoryEvent(
                            ExpensesCategoryEvent.DeleteExpensesCategory(
                                selectedExpensesCategoryItem
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
                expensesCategoryViewModel.onExpensesCategoryEvent(ExpensesCategoryEvent.RefreshExpenses)
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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    CircularProgressIndicator()
                }
            }else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(expensesCategories){ _, expensesCategory ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall)
                                .clickable {
                                    expensesCategoryViewModel.onExpensesCategoryEvent(
                                        ExpensesCategoryEvent.SelectExpensesCategory(
                                            expensesCategory.expensesCategoryId
                                        )
                                    )
                                },
                            shape = RoundedCornerShape(4.dp),
                            border = if(selectedExpensesCategoryItem == expensesCategory.expensesCategoryId)
                                BorderStroke(1.dp, MaterialTheme.colors.primary)
                            else null,
                            elevation = 2.dp,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(SpaceMedium)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.SpaceBetween,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = expensesCategory.expensesCategoryName,
                                    style = MaterialTheme.typography.body1,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}