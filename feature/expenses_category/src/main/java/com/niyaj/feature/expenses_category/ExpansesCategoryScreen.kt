package com.niyaj.feature.expenses_category

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ExpenseTestTags
import com.niyaj.common.tags.ExpenseTestTags.CREATE_NEW_EXPENSE_CATEGORY
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_CATEGORY_MESSAGE
import com.niyaj.common.tags.ExpenseTestTags.DELETE_EXPENSE_CATEGORY_TITLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_CATEGORY_NOT_AVAILABLE
import com.niyaj.common.tags.ExpenseTestTags.EXPENSE_CATEGORY_SCREEN_TITLE
import com.niyaj.common.tags.ExpenseTestTags.NO_ITEMS_IN_EXPENSE_CATEGORY
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.expenses_category.components.ExpensesCategoryData
import com.niyaj.feature.expenses_category.destinations.AddEditExpensesCategoryScreenDestination
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.ScaffoldNavActions
import com.niyaj.ui.components.StandardFabButton
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
 *  Expenses Category Screen
 *  @author Sk Niyaj Ali
 *  @param navController
 *  @param scaffoldState
 *  @param viewModel
 *  @param resultRecipient
 *  @see ExpensesCategoryViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.EXPENSES_CATEGORY_SCREEN)
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

    val uiState = viewModel.expensesCategories.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()

    val showFab = viewModel.totalItems.isNotEmpty()

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
        showBackButton = selectedItems.isEmpty(),
        selectionCount = selectedItems.size,
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        title = if (selectedItems.isEmpty()) EXPENSE_CATEGORY_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFabButton(
                text = CREATE_NEW_EXPENSE_CATEGORY.uppercase(),
                showScrollToTop = lazyGridState.isScrolled,
                visible = showFab && selectedItems.isEmpty() && !showSearchBar,
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
                placeholderText = ExpenseTestTags.EXPENSE_CATEGORY_SEARCH_PLACEHOLDER,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditExpensesCategoryScreenDestination(selectedItems.first()))
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
            label = "Expenses Category::State"
        ) { state ->
            when (state) {
                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (searchText.isEmpty()) EXPENSE_CATEGORY_NOT_AVAILABLE else NO_ITEMS_IN_EXPENSE_CATEGORY,
                        buttonText = CREATE_NEW_EXPENSE_CATEGORY,
                        onClick = {
                            navController.navigate(AddEditExpensesCategoryScreenDestination())
                        }
                    )
                }

                is UiState.Loading -> LoadingIndicator()

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(SpaceSmall)
                    ) {
                        items(
                            items = state.data,
                            key = { it.expensesCategoryId }
                        ) { category ->
                            ExpensesCategoryData(
                                category = category,
                                doesSelected = {selectedItems.contains(it)},
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
        title(text = DELETE_EXPENSE_CATEGORY_TITLE)
        message(text = DELETE_EXPENSE_CATEGORY_MESSAGE)
    }
}