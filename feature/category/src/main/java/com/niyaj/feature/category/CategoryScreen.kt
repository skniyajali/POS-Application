package com.niyaj.feature.category

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.FabPosition
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_NOT_AVAILABLE
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_SCREEN_TITLE
import com.niyaj.common.tags.CategoryTestTags.CATEGORY_SEARCH_PLACEHOLDER
import com.niyaj.common.tags.CategoryTestTags.CREATE_NEW_CATEGORY
import com.niyaj.common.tags.CategoryTestTags.DELETE_CATEGORY_ITEM_MESSAGE
import com.niyaj.common.tags.CategoryTestTags.DELETE_CATEGORY_ITEM_TITLE
import com.niyaj.common.utils.Constants.SEARCH_ITEM_NOT_FOUND
import com.niyaj.feature.category.components.CategoryData
import com.niyaj.feature.category.destinations.AddEditCategoryScreenDestination
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Category Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
// * @param resultRecipient
 * @see CategoryViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.CATEGORY_SCREEN)
@Composable
fun CategoryScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: CategoryViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCategoryScreenDestination, String>
) {
    val scope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val deleteCategoryState = rememberMaterialDialogState()

    val uiState = viewModel.categories.collectAsStateWithLifecycle().value

    val selectedCategories = viewModel.selectedItems.toList()

    val showFab = viewModel.totalItems.isNotEmpty()

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {
                if (selectedCategories.isNotEmpty()) {
                    viewModel.deselectItems()
                }
            }

            is NavResult.Value -> {
                if (selectedCategories.isNotEmpty()) {
                    viewModel.deselectItems()
                }

                scope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(result.value)
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else if (selectedCategories.isNotEmpty()) {
            viewModel.deselectItems()
        } else {
            navController.navigateUp()
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        selectionCount = selectedCategories.size,
        showBackButton = selectedCategories.isEmpty(),
        onBackClick = {
            if (showSearchBar) {
                viewModel.deselectItems()
            } else {
                navController.navigateUp()
            }
        },
        title = if (selectedCategories.isEmpty()) CATEGORY_SCREEN_TITLE else "${selectedCategories.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = lazyGridState.isScrolled,
                fabText = CREATE_NEW_CATEGORY.uppercase(),
                fabVisible = (showFab && selectedCategories.isEmpty() && !showSearchBar),
                onFabClick = {
                        navController.navigate(AddEditCategoryScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        lazyGridState.animateScrollToItem(0)
                    }
                }
            )
        },
        fabPosition = if (lazyGridState.isScrolled) FabPosition.End else FabPosition.Center,
        navActions = {
            ScaffoldNavActions(
                placeholderText = CATEGORY_SEARCH_PLACEHOLDER,
                selectionCount = selectedCategories.size,
                showSearchBar = showSearchBar,
                showSearchIcon = showFab,
                searchText = searchText,
                onEditClick = {
                        navController.navigate(AddEditCategoryScreenDestination(selectedCategories.first()))
                },
                onDeleteClick = {
                    deleteCategoryState.show()
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
            label = "Category::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = if (showSearchBar) SEARCH_ITEM_NOT_FOUND else CATEGORY_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_CATEGORY.uppercase(),
                        onClick = {
                            navController.navigate(AddEditCategoryScreenDestination())
                        }
                    )
                }

                is UiState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        state = lazyGridState,
                    ) {
                        items(
                            items = state.data,
                            key = {
                                it.categoryId
                            }
                        ) { item ->
                            CategoryData(
                                item = item,
                                doesSelected = {
                                    selectedCategories.contains(it)
                                },
                                onClick = {
                                    if (selectedCategories.isNotEmpty()) {
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
        dialogState = deleteCategoryState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteCategoryState.hide()
                    viewModel.deselectItems()
                },
            )
        }
    ) {
        title(text = DELETE_CATEGORY_ITEM_TITLE)
        message(text = DELETE_CATEGORY_ITEM_MESSAGE)
    }
}