package com.niyaj.popos.features.main_feed.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.StandardBackdropScaffold
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryEvent
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun MainFeedScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    mainFeedViewModel: MainFeedViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>
) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val scope = rememberCoroutineScope()

    val categories = mainFeedViewModel.categories.collectAsStateWithLifecycle().value.categories
    val categoriesIsLoading = mainFeedViewModel.categories.collectAsStateWithLifecycle().value.isLoading
    val categoriesHasError = mainFeedViewModel.categories.collectAsStateWithLifecycle().value.error
    val filterCategory = mainFeedViewModel.categories.collectAsStateWithLifecycle().value.filterCategory
    val selectedCategory = mainFeedViewModel.selectedCategory.value

    val products = mainFeedViewModel.products.collectAsStateWithLifecycle().value.products
    val productsIsLoading = mainFeedViewModel.products.collectAsStateWithLifecycle().value.isLoading
    val productsHasError = mainFeedViewModel.products.collectAsStateWithLifecycle().value.error
    val filterProduct = mainFeedViewModel.products.collectAsStateWithLifecycle().value.filterProduct

    val selectedOrder = mainFeedViewModel.selectedCartOrder.collectAsStateWithLifecycle().value
    val selectedOrderId = if (selectedOrder != null) {
        if(!selectedOrder.address?.addressName.isNullOrEmpty()){
            selectedOrder.address?.shortName?.uppercase().plus(" -").plus(selectedOrder.orderId)
        } else {
            selectedOrder.orderId
        }
    } else null

    val showSearchBar by mainFeedViewModel.toggledSearchBar.collectAsStateWithLifecycle()
    val searchText = mainFeedViewModel.searchText.collectAsStateWithLifecycle().value

    val productList = mainFeedViewModel.productsList.collectAsLazyPagingItems()

    LaunchedEffect(key1 = true){
        mainFeedViewModel.eventFlow.collectLatest { event ->
            when(event){
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
        if (showSearchBar){
            mainFeedViewModel.onSearchBarCloseAndClearClick()
        } else if(selectedCategory.isNotEmpty()) {
            mainFeedViewModel.onMainFeedCategoryEvent(MainFeedCategoryEvent.OnSelectCategory(selectedCategory))
        } else{
            navController.popBackStack()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                mainFeedViewModel.onEvent(MainFeedEvent.RefreshMainFeed)
            }
        }
    }

    LaunchedEffect(key1 = true){
        scope.launch {
            mainFeedViewModel.onEvent(MainFeedEvent.GetSelectedOrder)
        }
    }

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
            onOpenSheet(BottomSheetScreen.GetAndSelectCartOrderScreen)
        },
        onSearchButtonClick = {
            mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.ToggleSearchBar)
        },
        onSearchTextChanged = {
            mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.SearchProduct(it))
        },
        onClearClick = {
            mainFeedViewModel.onSearchTextClearClick()
        },
        onBackButtonClick = {
            if (showSearchBar){
                mainFeedViewModel.onSearchBarCloseAndClearClick()
            }else{
                navController.navigateUp()
            }
        },
        backLayerContent = {
            BackLayerContent(navController = navController, backdropScaffoldState)
        },
        frontLayerContent = {
            FrontLayerContent(
                categoriesIsLoading = categoriesIsLoading,
                productsIsLoading = productsIsLoading,
                productsHasError = productsHasError,
                categoriesHasError = categoriesHasError,
                onCategoryFilterClick = {
                    onOpenSheet(
                        BottomSheetScreen.FilterCategoryScreen(
                            filterCategory = filterCategory,
                            onFilterChanged = {
                                mainFeedViewModel.onMainFeedCategoryEvent(MainFeedCategoryEvent.OnFilterCategory(it))
                            },
                        )
                    )
                },
                categories = categories,
                onCategoryClick = {
                    mainFeedViewModel.onMainFeedCategoryEvent(MainFeedCategoryEvent.OnSelectCategory(it))
                },
                selectedCategory = selectedCategory,
                onProductFilterClick = {
                    onOpenSheet(
                        BottomSheetScreen.FilterProductScreen(
                            filterProduct = filterProduct,
                            onFilterChanged = {
                                mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.OnFilterProduct(it))
                            },
                        )
                    )
                },
                products = products,
                pagingProducts = productList,
                onProductLeftClick = {
                    if (selectedOrder != null) {
                        mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.RemoveProductFromCart(selectedOrder.cartOrderId, it))
                    }
                },
                onProductRightClick = {
                    if(selectedOrder == null){
                        navController.navigate(AddEditCartOrderScreenDestination())
                    }else{
                        mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.AddProductToCart(selectedOrder.cartOrderId, it))
                    }
                },
                onRefreshFrontLayer = {
                    mainFeedViewModel.onEvent(MainFeedEvent.RefreshMainFeed)
                }
            )
        },
    )
}