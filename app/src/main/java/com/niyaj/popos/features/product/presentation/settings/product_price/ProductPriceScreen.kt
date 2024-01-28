package com.niyaj.popos.features.product.presentation.settings.product_price

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Moving
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ImportExportHeader
import com.niyaj.popos.features.components.StandardButtonFW
import com.niyaj.popos.features.components.StandardOutlinedTextField
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.product.presentation.components.ProductBody
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet
import io.sentry.compose.SentryTraced

/**
 * Product Price Settings Screen
 * @author Sk Niyaj Ali
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ProductPriceScreen(
    type: String = "Increase",
    navController: NavController = rememberNavController(),
    productPriceViewModel: ProductPriceViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val isChoose = productPriceViewModel.onChoose

    val products = productPriceViewModel.products.collectAsStateWithLifecycle().value.products
    val groupedProducts = products.groupBy {
        it.category?.categoryName
    }

    val selectedProducts = productPriceViewModel.selectedProducts.toList()

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0 || lazyGridState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        productPriceViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    SentryTraced(tag = "ProductPriceScreen") {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxWidth(),
            text = if (type == "Increase")
                stringResource(id = R.string.increase_product_price)
            else
                stringResource(id = R.string.decrease_product_price),
            onClosePressed = {
                navController.navigateUp()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ImportExportHeader(
                    text = "Applied " + if(isChoose && selectedProducts.isNotEmpty()) "On ${selectedProducts.size} Selected Products" else "To All Products",
                    isChosen = isChoose,
                    onClickChoose = {
                        productPriceViewModel.onEvent(ProductPriceEvent.OnChooseProduct)
                    },
                    onClickAll = {
                        productPriceViewModel.onChoose = false
                        productPriceViewModel.onEvent(ProductPriceEvent.DeselectProducts)
                    },
                )

                AnimatedVisibility(
                    visible = isChoose,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (productsExpanded) Modifier.weight(1.1F) else Modifier),
                ) {

                    ProductBody(
                        lazyListState = lazyListState,
                        lazyGridState = lazyGridState,
                        groupedProducts = groupedProducts,
                        onCategorySelect = {
                            productPriceViewModel.onEvent(ProductPriceEvent.SelectProducts(it))
                        },
                        onSelectProduct = {
                            productPriceViewModel.onEvent(ProductPriceEvent.SelectProduct(it))
                        },
                        onExpandChange = {
                            productsExpanded = !productsExpanded
                        },
                        selectedProducts = selectedProducts,
                        showScrollToTop = showScrollToTop.value,
                        productsExpanded = productsExpanded,
                        selectedProductsSize = selectedProducts.size,
                        onClickSelectAll = {
                            productPriceViewModel.onEvent(ProductPriceEvent.SelectAllProduct)
                        },
                    )
                }

                StandardOutlinedTextField(
                    modifier = Modifier,
                    text = productPriceViewModel.productPrice.value.productPrice,
                    label = "Product Price",
                    leadingIcon = Icons.Default.CurrencyRupee,
                    keyboardType = KeyboardType.Number,
                    error = productPriceViewModel.productPrice.value.productPriceError,
                    onValueChange = {
                        productPriceViewModel.onEvent(ProductPriceEvent.OnPriceChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier.fillMaxWidth(),
                    text = if (type == "Increase")
                        stringResource(id = R.string.increase_product_price).uppercase()
                    else
                        stringResource(id = R.string.decrease_product_price).uppercase(),
                    icon = if (type == "Increase") Icons.Default.Moving else Icons.AutoMirrored.Filled.TrendingDown,
                    onClick = {
                        if (type == "Increase") {
                            productPriceViewModel.onEvent(ProductPriceEvent.IncreaseProductPrice)
                        } else {
                            productPriceViewModel.onEvent(ProductPriceEvent.DecreaseProductPrice)
                        }
                    },
                )
            }
        }
    }
}