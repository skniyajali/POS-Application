package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.components.StandardBackdropScaffold
import com.niyaj.popos.features.main_feed.presentation.components.category.MainFeedCategoryEvent
import com.niyaj.popos.features.main_feed.presentation.components.product.MainFeedProductEvent
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainFeedScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    mainFeedViewModel: MainFeedViewModel = hiltViewModel(),
) {
    val backdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Concealed)
    val scope = rememberCoroutineScope()

    val categories by lazy { mainFeedViewModel.categories.value.categories }
    val categoriesIsLoading by lazy { mainFeedViewModel.categories.value.isLoading }
    val categoriesHasError by lazy { mainFeedViewModel.categories.value.error }
    val filterCategory by lazy { mainFeedViewModel.categories.value.filterCategory }
    val selectedCategory by lazy { mainFeedViewModel.selectedCategory.value}

    val products = mainFeedViewModel.products.collectAsState().value.products
    val productsIsLoading = mainFeedViewModel.products.collectAsState().value.isLoading
    val productsHasError = mainFeedViewModel.products.collectAsState().value.error
    val filterProduct = mainFeedViewModel.products.collectAsState().value.filterProduct

    val selectedOrderId = mainFeedViewModel.selectedCartOrder.collectAsState().value

    val showSearchBar by mainFeedViewModel.toggledSearchBar.collectAsState()
    val searchText = mainFeedViewModel.searchText.collectAsState().value

    val productList = mainFeedViewModel.productsList.collectAsLazyPagingItems()

    StandardBackdropScaffold(
        navController = navController,
        backdropScaffoldState = backdropScaffoldState,
        scaffoldState = scaffoldState,
        scope = scope,
        onSelectedOrderClick = {
            onOpenSheet(BottomSheetScreen.GetAndSelectCartOrderScreen)
        },
        selectedOrderId = if(!selectedOrderId.address?.addressName.isNullOrEmpty()){
            selectedOrderId.address?.shortName?.uppercase().plus(" -")
                .plus(selectedOrderId.orderId)
        } else {
            selectedOrderId.orderId
        },
        showSearchBar = showSearchBar,
        onSearchButtonClick = {
            mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.ToggleSearchBar)
        },
        searchText = searchText,
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
                    mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.RemoveProductFromCart(selectedOrderId.cartOrderId, it))
                },
                onProductRightClick = {
                    if(selectedOrderId.orderId.isEmpty()){
                        onOpenSheet(BottomSheetScreen.CreateCartOrderScreen)
                    }else{
                        mainFeedViewModel.onMainFeedProductEvent(MainFeedProductEvent.AddProductToCart(selectedOrderId.cartOrderId, it))
                    }
                }
            )
        },
    )
}