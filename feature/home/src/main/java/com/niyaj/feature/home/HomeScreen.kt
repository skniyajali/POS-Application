package com.niyaj.feature.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.feature.home.components.category.MainFeedCategoryEvent
import com.niyaj.feature.home.components.product.MainFeedProductEvent
import com.niyaj.ui.components.StandardBackdropScaffold
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Displays the main feed of the app.
 * @author Sk Niyaj Ali
 *
 * @param navController A reference to the NavController, which is used to navigate to other screens in the app.
 * @param scaffoldState A reference to the ScaffoldState, which is used to manage the app's top-level UI.
 * @param viewModel A reference to the MainFeedViewModel, which is the view model for the MainFeedScreen.
 * @see HomeViewModel
 */
@RootNavGraph(start = true)
@Destination(route = Screens.HOME_SCREEN)
@Composable
fun HomeScreen(
    navController: NavController = rememberNavController(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val categoryLazyListState = rememberLazyListState()

    val categories = viewModel.categories.collectAsStateWithLifecycle().value.categories
    val categoriesIsLoading = viewModel.categories.collectAsStateWithLifecycle().value.isLoading
    val categoriesHasError = viewModel.categories.collectAsStateWithLifecycle().value.error
    val selectedCategory = viewModel.selectedCategory.value

    val products = viewModel.products.collectAsStateWithLifecycle().value.products
    val productsIsLoading = viewModel.products.collectAsStateWithLifecycle().value.isLoading
    val productsHasError = viewModel.products.collectAsStateWithLifecycle().value.error

    val selectedOrder = viewModel.selectedCartOrder.collectAsStateWithLifecycle().value
    val selectedOrderId = if (selectedOrder != null) {
        if (!selectedOrder.address?.addressName.isNullOrEmpty()) {
            selectedOrder.address?.addressName?.plus(" -").plus(selectedOrder.orderId)
        } else {
            selectedOrder.orderId
        }
    } else null

    val showSearchBar by viewModel.toggledSearchBar.collectAsStateWithLifecycle()
    val searchText = viewModel.searchText.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = selectedOrderId) {
        scope.launch {
            if (lazyListState.isScrolled) {
                lazyListState.animateScrollToItem(0)
            }
            if (categoryLazyListState.isScrolled) {
                categoryLazyListState.animateScrollToItem(0)
            }
        }
    }

    LaunchedEffect(key1 = selectedCategory) {
        scope.launch {
            lazyListState.animateScrollToItem(0)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.successMessage)
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.onSearchBarCloseAndClearClick()
        } else if (selectedCategory.isNotEmpty()) {
            viewModel.onCategoryEvent(MainFeedCategoryEvent.OnSelectCategory(selectedCategory))
        } else {
            navController.popBackStack()
        }
    }

    StandardBackdropScaffold(
        scaffoldState = scaffoldState,
        selectedOrderId = selectedOrderId,
        showSearchBar = showSearchBar,
        searchText = searchText,
        showFloatingActionButton = !showSearchBar && products.isNotEmpty(),
        onSelectedOrderClick = {
            navController.navigate(Screens.SELECT_ORDER_SCREEN)
        },
        onSearchButtonClick = {
            viewModel.onProductEvent(MainFeedProductEvent.ToggleSearchBar)
        },
        onSearchTextChanged = {
            viewModel.onProductEvent(MainFeedProductEvent.SearchProduct(it))
        },
        onClearClick = {
            viewModel.onSearchTextClearClick()
        },
        onBackButtonClick = {
            if (showSearchBar) {
                viewModel.onSearchBarCloseAndClearClick()
            } else {
                navController.navigateUp()
            }
        },
        backLayerContent = {
            BackLayerContent(navController = navController)
        },
        frontLayerContent = { paddingValues ->
            FrontLayerContent(
                modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                categoriesIsLoading = categoriesIsLoading,
                productsIsLoading = productsIsLoading,
                productsHasError = productsHasError,
                categoriesHasError = categoriesHasError,
                lazyListState = lazyListState,
                categoryLazyListState = categoryLazyListState,
                categories = categories,
                selectedCategory = selectedCategory,
                products = products,
                onCategoryClick = {
                    viewModel.onCategoryEvent(MainFeedCategoryEvent.OnSelectCategory(it))
                },
                onProductLeftClick = {
                    if (selectedOrder != null) {
                        viewModel.onProductEvent(
                            MainFeedProductEvent.RemoveProductFromCart(
                                selectedOrder.cartOrderId,
                                it
                            )
                        )
                    }
                },
                onProductRightClick = {
                    if (selectedOrder == null) {
                        navController.navigate(Screens.ADD_EDIT_CART_ORDER_SCREEN)
                    } else {
                        viewModel.onProductEvent(
                            MainFeedProductEvent.AddProductToCart(selectedOrder.cartOrderId, it)
                        )
                    }
                },
                onRefreshFrontLayer = {
                    viewModel.onEvent(HomeEvent.RefreshHome)
                },
                onNavigateToProductScreen = {
                    navController.navigate(Screens.PRODUCT_SCREEN)
                }
            )
        },
        currentRoute = Screens.HOME_SCREEN,
        onNavigateToScreen = {
            navController.navigate(it)
        },
        onClickLogOut = {
            // TODO:: Implement Logout functionalities
        }
    )

}