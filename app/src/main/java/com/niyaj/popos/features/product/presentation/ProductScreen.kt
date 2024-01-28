package com.niyaj.popos.features.product.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.ScaffoldNavActions
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditProductScreenDestination
import com.niyaj.popos.features.destinations.ProductSettingScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.category.CategoryItems
import com.niyaj.popos.features.product.presentation.components.ProductBodyContent
import com.niyaj.popos.features.product.presentation.components.ViewType.COLUMN
import com.niyaj.popos.features.product.presentation.components.ViewType.ROW
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

/**
 * Product Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param productsViewModel
 * @param resultRecipient
 * @see ProductsViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ProductScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    productsViewModel: ProductsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>
) {
    val categoryState = rememberLazyListState()
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()

    val deleteProductState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val products = productsViewModel.products.collectAsStateWithLifecycle().value.products
    val isLoading = productsViewModel.products.collectAsStateWithLifecycle().value.isLoading
    val error = productsViewModel.products.collectAsStateWithLifecycle().value.error
    val groupedProducts = products.groupBy {
        it.category?.categoryName
    }

    val selectedProducts = productsViewModel.selectedProducts
    val selectedCategory = productsViewModel.selectedCategory.collectAsStateWithLifecycle().value

    // Remember a SystemUiController
    val systemUiController = rememberSystemUiController()

    val transition = updateTransition(selectedProducts.isNotEmpty(), label = "isContextual")

    val statusBarColor by transition.animateColor(label = "statusBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }
    val backgroundColor by transition.animateColor(label = "actionBarContextual") { isContextualMode ->
        if(isContextualMode) { MaterialTheme.colors.secondary } else { MaterialTheme.colors.primary }
    }

    val showSearchBar = productsViewModel.toggledSearchBar.collectAsState().value
    val searchText = productsViewModel.searchText.collectAsStateWithLifecycle().value

    val viewType = productsViewModel.viewType.collectAsStateWithLifecycle().value

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyGridState.firstVisibleItemIndex > 0
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {
                if(selectedProducts.isNotEmpty()){
                    productsViewModel.onProductEvent(ProductEvent.DeselectProducts)
                }
            }
            is NavResult.Value -> {
                if(selectedProducts.isNotEmpty()){
                    productsViewModel.onProductEvent(ProductEvent.DeselectProducts)
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
            productsViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedProducts.isNotEmpty()) {
            productsViewModel.onProductEvent(ProductEvent.DeselectProducts)
        } else{
            navController.navigateUp()
        }
    }

    LaunchedEffect(key1 = true) {
        productsViewModel.eventFlow.collect { event ->
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

                is UiEvent.IsLoading -> {}
            }
        }
    }

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    SentryTraced(tag = "ProductScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = selectedProducts.isEmpty(),
            onBackButtonClick = {
                if (showSearchBar){
                    productsViewModel.onSearchBarCloseAndClearClick()
                }else{
                    navController.navigateUp()
                }
            },
            title = {
                if (selectedProducts.isEmpty()) {
                    Text(text = "Products")
                } else if(selectedProducts.size > 1){
                    Text(text = "${selectedProducts.size} Selected")
                }
            },
            isFloatingActionButtonDocked = products.isNotEmpty(),
            floatingActionButton = {
                StandardFabButton(
                    text = stringResource(id = R.string.create_product).uppercase(),
                    showScrollToTop = showScrollToTop.value,
                    visible = products.isNotEmpty() && selectedProducts.isEmpty() && !showSearchBar,
                    onScrollToTopClick = {
                        scope.launch {
                            when(viewType) {
                                ROW -> lazyGridState.animateScrollToItem(0)
                                COLUMN -> lazyListState.animateScrollToItem(index = 0)
                            }
                        }
                    },
                    onClick = {
                        navController.navigate(AddEditProductScreenDestination())
                    },
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
            navActions = {
                ScaffoldNavActions(
                    multiSelect = true,
                    allItemsIsEmpty = products.isEmpty(),
                    selectedItems = selectedProducts,
                    onClickEdit = {
                        navController.navigate(AddEditProductScreenDestination(productId = selectedProducts.first()))
                    },
                    onClickDelete = {
                        deleteProductState.show()
                    },
                    onClickSelectAll = {
                        productsViewModel.onProductEvent(ProductEvent.SelectAllProduct)
                    },
                    showSearchBar = showSearchBar,
                    searchText = searchText,
                    onSearchTextChanged = {
                        productsViewModel.onProductEvent(ProductEvent.OnSearchProduct(it))
                    },
                    onClearClick = {
                        productsViewModel.onSearchTextClearClick()
                    },
                    onClickSearch = {
                        productsViewModel.onProductEvent(ProductEvent.ToggleSearchBar)
                    },
                    showSettingsIcon = true,
                    onClickSetting = {
                        navController.navigate(ProductSettingScreenDestination())
                    },
                    content = {
                        if (products.isNotEmpty()) {
                            if (viewType == COLUMN) {
                                IconButton(
                                    onClick = {
                                        productsViewModel.onProductEvent(
                                            ProductEvent.OnChangeViewType(
                                                ROW
                                            )
                                        )
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
                                        productsViewModel.onProductEvent(
                                            ProductEvent.OnChangeViewType(
                                                COLUMN
                                            )
                                        )
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
            navigationIcon = {
                if(selectedProducts.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            productsViewModel.onProductEvent(ProductEvent.DeselectProducts)
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
                dialogState = deleteProductState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            productsViewModel.onProductEvent(ProductEvent.DeleteProducts(selectedProducts.toList()))
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteProductState.hide()
                        },
                    )
                }
            ) {
                title(text = "Delete ${selectedProducts.size} Products?")
                message(res = R.string.delete_product_message)
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    productsViewModel.onProductEvent(ProductEvent.RefreshProduct)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(SpaceSmall),
                ) {
                    if ((products.isEmpty() && selectedCategory.isEmpty()) || error != null) {
                        ItemNotAvailable(
                            text = error ?: stringResource(id = R.string.no_items_in_product),
                            buttonText = stringResource(id = R.string.create_product).uppercase(),
                            onClick = {
                                navController.navigate(AddEditProductScreenDestination())
                            }
                        )
                    }  else if(isLoading){
                        LoadingIndicator()
                    } else {
                        CategoryItems(
                            lazyListState = categoryState,
                            categories = productsViewModel.categories.collectAsStateWithLifecycle().value,
                            selectedCategory = selectedCategory,
                            onClickCategory = {
                                productsViewModel.onProductEvent(ProductEvent.SelectCategory(it))
                            },
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        ProductBodyContent(
                            lazyListState = lazyListState,
                            lazyGridState = lazyGridState,
                            groupedProducts = groupedProducts,
                            selectedProducts = selectedProducts,
                            viewType = viewType,
                            showScrollToTop = showScrollToTop.value,
                            onCategorySelect = {
                                productsViewModel.onProductEvent(ProductEvent.SelectProducts(it))
                            },
                            onSelectProduct = {
                                productsViewModel.onProductEvent(ProductEvent.SelectProduct(it))
                            },
                            onClickProduct = {
                                navController.navigate(
                                    com.niyaj.popos.features.destinations.ProductDetailsScreenDestination(
                                        it
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