package com.niyaj.feature.product.settings.product_price

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.ProductTestTags
import com.niyaj.common.tags.ProductTestTags.DECREASE_PRODUCTS_TITLE
import com.niyaj.common.tags.ProductTestTags.INCREASE_PRODUCTS_TEXT_FIELD
import com.niyaj.common.tags.ProductTestTags.INCREASE_PRODUCTS_TITLE
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.feature.product.components.ProductBody
import com.niyaj.feature.product.destinations.AddEditProductScreenDestination
import com.niyaj.ui.components.ImportExportHeader
import com.niyaj.ui.components.ItemNotAvailableHalf
import com.niyaj.ui.components.StandardButtonFW
import com.niyaj.ui.components.StandardOutlinedTextField
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 * Product Price Settings Screen
 * @author Sk Niyaj Ali
 */
@Destination(style = DestinationStyleBottomSheet::class)
@Composable
fun ProductPriceScreen(
    type: String = "Increase",
    navController: NavController = rememberNavController(),
    viewModel: ProductPriceViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {
    val lazyListState = rememberLazyListState()
    val lazyGridState = rememberLazyGridState()
    val isChoose = viewModel.onChoose

    val products = viewModel.products.collectAsStateWithLifecycle().value
    val groupedProducts = remember(products) {
        products.groupBy { it.category?.categoryName }
    }

    val selectedProducts = viewModel.selectedProducts.toList()

    val priceError = viewModel.priceError.collectAsStateWithLifecycle().value

    var productsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.Success -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.Error -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }
            }
        }
    }

    val title = if (type == "Increase") INCREASE_PRODUCTS_TITLE else DECREASE_PRODUCTS_TITLE

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxWidth(),
        text = title,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        if (products.isEmpty()) {
            ItemNotAvailableHalf(
                modifier = Modifier.fillMaxWidth(),
                text = ProductTestTags.NO_ITEMS_IN_PRODUCT,
                buttonText = ProductTestTags.CREATE_NEW_PRODUCT,
                onClick = {
                    navController.navigate(AddEditProductScreenDestination())
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ImportExportHeader(
                    text = "Applied " + if (isChoose && selectedProducts.isNotEmpty())
                        "On ${selectedProducts.size} Selected Products" else "To All Products",
                    isChosen = isChoose,
                    onClickChoose = {
                        viewModel.onEvent(ProductPriceEvent.OnChooseProduct)
                    },
                    onClickAll = {
                        viewModel.onChoose = false
                        viewModel.onEvent(ProductPriceEvent.DeselectProducts)
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
                            viewModel.onEvent(ProductPriceEvent.SelectProducts(it))
                        },
                        onSelectProduct = {
                            viewModel.onEvent(ProductPriceEvent.SelectProduct(it))
                        },
                        onExpandChange = {
                            productsExpanded = !productsExpanded
                        },
                        selectedProducts = selectedProducts,
                        productsExpanded = productsExpanded,
                        selectedProductsSize = selectedProducts.size,
                        onClickSelectAll = {
                            viewModel.onEvent(ProductPriceEvent.SelectAllProduct)
                        },
                    )
                }

                StandardOutlinedTextField(
                    modifier = Modifier.testTag(INCREASE_PRODUCTS_TEXT_FIELD),
                    text = viewModel.productPrice.value,
                    label = INCREASE_PRODUCTS_TEXT_FIELD,
                    leadingIcon = Icons.Default.CurrencyRupee,
                    keyboardType = KeyboardType.Number,
                    error = priceError,
                    onValueChange = {
                        viewModel.onEvent(ProductPriceEvent.OnPriceChanged(it))
                    },
                )

                Spacer(modifier = Modifier.height(SpaceMedium))

                StandardButtonFW(
                    modifier = Modifier.fillMaxWidth(),
                    text = title.uppercase(),
                    enabled = priceError == null,
                    icon = if (type == "Increase") Icons.Default.Moving else Icons.AutoMirrored.Filled.TrendingDown,
                    onClick = {
                        if (type == "Increase") {
                            viewModel.onEvent(ProductPriceEvent.IncreaseProductPrice)
                        } else {
                            viewModel.onEvent(ProductPriceEvent.DecreaseProductPrice)
                        }
                    },
                )
            }
        }
    }

}