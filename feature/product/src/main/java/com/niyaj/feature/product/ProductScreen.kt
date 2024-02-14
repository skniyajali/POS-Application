package com.niyaj.feature.product

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_MESSAGE
import com.niyaj.common.tags.ProductTestTags.DELETE_PRODUCT_TITLE
import com.niyaj.common.tags.ProductTestTags.NO_ITEMS_IN_PRODUCT
import com.niyaj.common.tags.ProductTestTags.PRODUCT_NOT_AVAILABLE
import com.niyaj.common.tags.ProductTestTags.PRODUCT_SCREEN_TITLE
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.product.components.ProductBodyContent
import com.niyaj.feature.product.components.ViewType
import com.niyaj.feature.product.destinations.AddEditProductScreenDestination
import com.niyaj.feature.product.destinations.ProductDetailsScreenDestination
import com.niyaj.feature.product.destinations.ProductSettingScreenDestination
import com.niyaj.ui.components.CategoryItems
import com.niyaj.ui.components.ItemNotAvailableHalf
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
 * Product Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param resultRecipient
 * @see ProductsViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.PRODUCT_SCREEN)
@Composable
fun ProductScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    viewModel: ProductsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>
) {
    val categoryState = rememberLazyListState()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    val deleteProductState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val uiState = viewModel.products.collectAsStateWithLifecycle().value
    val categories = viewModel.categories.collectAsStateWithLifecycle().value

    val selectedItems = viewModel.selectedItems.toList()
    val selectedCategory = viewModel.selectedCategory.collectAsStateWithLifecycle().value

    val showFab = viewModel.totalItems.isNotEmpty() && selectedCategory.isEmpty()

    val showSearchBar = viewModel.showSearchBar.collectAsState().value
    val searchText = viewModel.searchText.value

    val viewType = viewModel.viewType.collectAsStateWithLifecycle().value

    val isScrolled = when(viewType) {
        ViewType.ROW -> lazyGridState.isScrolled
        ViewType.COLUMN -> lazyListState.isScrolled
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

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
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
        title = if (selectedItems.isEmpty()) PRODUCT_SCREEN_TITLE else "${selectedItems.size} Selected",
        showFab = showFab,
        floatingActionButton = {
            StandardFAB(
                showScrollToTop = isScrolled,
                fabText = ProductTestTags.CREATE_NEW_PRODUCT,
                fabVisible = (showFab && selectedItems.isEmpty() && !showSearchBar),
                onFabClick = {
                    navController.navigate(AddEditProductScreenDestination())
                },
                onClickScroll = {
                    scope.launch {
                        when(viewType) {
                            ViewType.ROW -> lazyGridState.animateScrollToItem(0)
                            ViewType.COLUMN -> lazyListState.animateScrollToItem(0)
                        }
                    }
                }
            )
        },
        fabPosition = if (isScrolled) FabPosition.End else FabPosition.Center,
        navActions = {
            ScaffoldNavActions(
                placeholderText = ProductTestTags.PRODUCT_SEARCH_PLACEHOLDER,
                showSettingsIcon = true,
                selectionCount = selectedItems.size,
                showSearchIcon = showFab,
                showSearchBar = showSearchBar,
                searchText = searchText,
                onEditClick = {
                    navController.navigate(AddEditProductScreenDestination(selectedItems.first()))
                },
                onDeleteClick = {
                    deleteProductState.show()
                },
                onSettingsClick = {
                    navController.navigate(ProductSettingScreenDestination())
                },
                onSelectAllClick = viewModel::selectAllItems,
                onClearClick = viewModel::clearSearchText,
                onSearchClick = viewModel::openSearchBar,
                onSearchTextChanged = viewModel::searchTextChanged,
                content = {
                    if (showFab) {
                        if (viewType == ViewType.COLUMN) {
                            IconButton(
                                onClick = {
                                    viewModel.onChangeViewType(ViewType.ROW)
                                },
                                modifier = Modifier.testTag("ViewTypeRow")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.GridView,
                                    contentDescription = stringResource(id = R.string.setting_icon),
                                    tint = MaterialTheme.colors.onPrimary,
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    viewModel.onChangeViewType(ViewType.COLUMN)

                                },
                                modifier = Modifier.testTag("ViewTypeColumn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.List,
                                    contentDescription = stringResource(id = R.string.setting_icon),
                                    tint = MaterialTheme.colors.onPrimary,
                                )
                            }
                        }
                    }
                }
            )
        },
        onDeselect = viewModel::deselectItems
    ) {
        Column(
            modifier = Modifier
                .padding(SpaceSmall),
        ) {
            CategoryItems(
                lazyListState = categoryState,
                categories = categories,
                selectedCategory = selectedCategory,
                onClickCategory = viewModel::selectCategory,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))


            Crossfade(
                targetState = uiState,
                label = "Product::State"
            ) { state ->
                when (state) {
                    is UiState.Loading -> LoadingIndicator()

                    is UiState.Empty -> {
                        ItemNotAvailableHalf(
                            modifier = Modifier.weight(2f),
                            text = if (searchText.isEmpty()) PRODUCT_NOT_AVAILABLE else NO_ITEMS_IN_PRODUCT,
                            buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                            onClick = {
                                navController.navigate(AddEditProductScreenDestination())
                            }
                        )
                    }

                    is UiState.Success -> {
                        val groupedProducts = remember(state.data) {
                            state.data.groupBy { it.category?.categoryName }
                        }

                        ProductBodyContent(
                            lazyListState = lazyListState,
                            lazyGridState = lazyGridState,
                            groupedProducts = groupedProducts,
                            selectedProducts = selectedItems,
                            viewType = viewType,
                            onCategorySelect = viewModel::selectProducts,
                            onSelectProduct = viewModel::selectItem,
                            onClickProduct = {
                                navController.navigate(ProductDetailsScreenDestination(it))
                            }
                        )
                    }
                }
            }
        }
    }

    MaterialDialog(
        dialogState = deleteProductState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = viewModel::deleteItems
            )
            negativeButton(
                text = "Cancel",
                onClick = {
                    deleteProductState.hide()
                },
            )
        }
    ) {
        title(text = DELETE_PRODUCT_TITLE)
        message(text = DELETE_PRODUCT_MESSAGE)
    }

}