package com.niyaj.popos.features.main_feed.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardBackdropScaffold
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.SelectedCartOrderScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryEvent
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductEvent
import com.niyaj.popos.utils.isScrolled
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 *  Main Feed Screen
 *  @author Sk Niyaj Ali
 *  @param navController
 *  @param viewModel
 *  @param resultRecipient
 *  @see MainFeedViewModel
 */
@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainFeedScreen(
    navController : NavController,
    scaffoldState : ScaffoldState,
    viewModel : MainFeedViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<AddEditCartOrderScreenDestination, String>,
) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
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
            selectedOrder.address?.shortName?.uppercase().plus(" -").plus(selectedOrder.orderId)
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
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(message = event.successMessage)
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvent.IsLoading -> {}
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

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                viewModel.onEvent(MainFeedEvent.RefreshMainFeed)
            }
        }
    }
    
    SentryTraced("main_feed_screen") {
        StandardBackdropScaffold(
            navController = navController,
            backdropScaffoldState = backdropScaffoldState,
            scaffoldState = scaffoldState,
            scope = scope,
            selectedOrderId = selectedOrderId,
            showSearchBar = showSearchBar,
            searchText = searchText,
            showFloatingActionButton = !showSearchBar && products.isNotEmpty(),
            onSelectedOrderClick = {
                navController.navigate(SelectedCartOrderScreenDestination)
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
            frontLayerContent = {
                FrontLayerContent(
                    navController = navController,
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
                                MainFeedProductEvent.RemoveProductFromCart(selectedOrder.cartOrderId, it)
                            )
                        }
                    },
                    onProductRightClick = {
                        if (selectedOrder == null) {
                            navController.navigate(AddEditCartOrderScreenDestination())
                        } else {
                            viewModel.onProductEvent(
                                MainFeedProductEvent.AddProductToCart(selectedOrder.cartOrderId, it)
                            )
                        }
                    },
                    onRefreshFrontLayer = {
                        viewModel.onEvent(MainFeedEvent.RefreshMainFeed)
                    }
                )
            },
        )
    }
}