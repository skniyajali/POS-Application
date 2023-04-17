package com.niyaj.popos.features.product.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.*
import com.niyaj.popos.features.destinations.AddEditProductScreenDestination
import com.niyaj.popos.features.destinations.ProductSettingScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.category.CategoryItems
import com.niyaj.popos.features.product.presentation.components.ProductCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Destination
@Composable
fun ProductScreen(
    onOpenSheet: (BottomSheetScreen) -> Unit = {},
    navController: NavController,
    scaffoldState: ScaffoldState,
    productsViewModel: ProductsViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditProductScreenDestination, String>
) {

    val lazyListState = rememberLazyListState()
    val deleteProductState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val products = productsViewModel.products.collectAsStateWithLifecycle().value.products
    val filterProduct = productsViewModel.products.collectAsStateWithLifecycle().value.filterProduct
    val isLoading = productsViewModel.products.collectAsStateWithLifecycle().value.isLoading
    val error = productsViewModel.products.collectAsStateWithLifecycle().value.error
    val groupedProducts = products.groupBy {
        it.category?.categoryName
    }

    val selectedProducts = productsViewModel.selectedProducts
    val selectedCategory = productsViewModel.selectedCategory.value

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

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
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
                is UiEvent.OnSuccess -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage
                    )
                }

                is UiEvent.OnError -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

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
            ExtendedFabButton(
                text = stringResource(id = R.string.create_product).uppercase(),
                showScrollToTop = showScrollToTop.value,
                visible = products.isNotEmpty() && selectedProducts.isEmpty() && !showSearchBar,
                onScrollToTopClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index = 0)
                    }
                },
                onClick = {
                    navController.navigate(AddEditProductScreenDestination())
                },
            )
        },
        floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        navActions = {
            if(selectedProducts.isNotEmpty()) {
                if(selectedProducts.size == 1){
                    IconButton(
                        onClick = {
                            navController.navigate(AddEditProductScreenDestination(productId = selectedProducts.first()))
                        },
                    ){
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Product",
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        deleteProductState.show()
                    },
                    enabled = selectedProducts.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Product",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }

                IconButton(
                    onClick = {
                        productsViewModel.onProductEvent(ProductEvent.SelectAllProduct)
                    },
                    enabled = selectedProducts.isNotEmpty()
                ){
                    Icon(
                        imageVector = Icons.Default.Rule,
                        contentDescription = "Select All Product",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
            else if(showSearchBar){
                StandardSearchBar(
                    searchText = productsViewModel.searchText.collectAsStateWithLifecycle().value,
                    placeholderText = "Search for products",
                    onSearchTextChanged = {
                        productsViewModel.onProductEvent(ProductEvent.OnSearchProduct(it))
                    },
                    onClearClick = {
                        productsViewModel.onSearchTextClearClick()
                    },
                )
            }
            else {
                if (products.isNotEmpty()){
                    IconButton(
                        onClick = {
                            productsViewModel.onProductEvent(ProductEvent.ToggleSearchBar)
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
                                BottomSheetScreen.FilterProductScreen(
                                    filterProduct = filterProduct,
                                    onFilterChanged = {
                                        productsViewModel.onProductEvent(ProductEvent.OnFilterProduct(it))
                                    },
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = stringResource(id = R.string.filter_product),
                            tint = MaterialTheme.colors.onPrimary,
                        )
                    }
                }

                IconButton(
                    onClick = {
                        navController.navigate(ProductSettingScreenDestination())
                    },
                    enabled = true,
                ){
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Products Settings",
                        tint = MaterialTheme.colors.onPrimary,
                    )
                }
            }
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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ){
                        CircularProgressIndicator()
                    }
                } else {
                    CategoryItems(
                        categories = productsViewModel.categories.collectAsState().value,
                        selectedCategory = productsViewModel.selectedCategory.value,
                        onClick = {
                            productsViewModel.onProductEvent(ProductEvent.SelectCategory(it))
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceSmall))

                    LazyColumn(
                        state = lazyListState,
                    ){
                        groupedProducts.forEach{ (category, productsNew) ->
                            stickyHeader {
                                TextWithCount(
                                    modifier = Modifier
                                        .background(
                                            if (showScrollToTop.value)
                                                MaterialTheme.colors.onPrimary
                                            else Color.Transparent
                                        )
                                        .clip(
                                            RoundedCornerShape(if (showScrollToTop.value) 4.dp else 0.dp)
                                        ),
                                    text = category ?: "Uncategorized",
                                    count = productsNew.count(),
                                    onClick = {
                                        productsViewModel.onProductEvent(ProductEvent.SelectProducts(productsNew.map { it.productId }))
                                    }
                                )
                            }

                            itemsIndexed(
                                items = productsNew,
                            ){ index, product ->
                                ProductCard(
                                    productName = product.productName,
                                    productPrice = product.productPrice.toString(),
                                    productCategoryName = product.category?.categoryName!!,
                                    doesSelected = selectedProducts.contains(product.productId),
                                    isAvailable = product.productAvailability,
                                    onSelectProduct = {
                                        productsViewModel.onProductEvent(ProductEvent.SelectProduct(product.productId))
                                    },
                                )

                                Spacer(modifier = Modifier.height(SpaceSmall))

                                if(index == products.size - 1) {
                                    Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}