package com.niyaj.popos.presentation.product.settings.product_price

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Rule
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.R
import com.niyaj.popos.domain.util.UiEvent
import com.niyaj.popos.presentation.components.*
import com.niyaj.popos.presentation.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.presentation.product.components.ProductCard
import com.niyaj.popos.presentation.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.result.ResultBackNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun ProductPriceScreen(
    type: String = "Increase",
    navController: NavController = rememberNavController(),
    productPriceViewModel: ProductPriceViewModel = hiltViewModel(),
    resultBackNavigator: ResultBackNavigator<String>
) {

    val lazyListState = rememberLazyListState()
    val isChoose = productPriceViewModel.onChoose

    val products = productPriceViewModel.products.collectAsState().value.products
    val groupedProducts = productPriceViewModel.products.collectAsState().value.products.groupBy {
        it.category
    }

    val selectedProducts = productPriceViewModel.selectedProducts.toList()

    var productsExpanded by remember {
        mutableStateOf(false)
    }

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    LaunchedEffect(key1 = true) {
        productPriceViewModel.eventFlow.collect { event ->
            when (event) {
                is UiEvent.OnSuccess -> {
                    resultBackNavigator.navigateBack(event.successMessage)
                }

                is UiEvent.OnError -> {
                    resultBackNavigator.navigateBack(event.errorMessage)
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Applied " + if(isChoose && selectedProducts.isNotEmpty()) "On ${selectedProducts.size} Selected Products" else "To All Products",
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    StandardChip(
                        text = "All",
                        isToggleable = true,
                        isSelected = !isChoose,
                        onClick = {
                            productPriceViewModel.onChoose = false
                            productPriceViewModel.onEvent(ProductPriceEvent.DeselectProducts)
                        }
                    )

                    Spacer(modifier = Modifier.width(SpaceMini))

                    StandardChip(
                        text = "Choose",
                        isToggleable = true,
                        isSelected = isChoose,
                        onClick = {
                            productPriceViewModel.onEvent(ProductPriceEvent.OnChooseProduct)
                        }
                    )
                }
            }

            AnimatedVisibility(
                visible = isChoose,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (productsExpanded) Modifier.weight(1.1F) else Modifier),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            productsExpanded = !productsExpanded
                        },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    StandardExpandable(
                        onExpandChanged = {
                            productsExpanded = !productsExpanded
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SpaceSmall),
                        expanded = productsExpanded,
                        title = {
                            TextWithIcon(
                                text = if(selectedProducts.isNotEmpty()) "${selectedProducts.size} Selected" else "Select Products",
                                icon = Icons.Default.Dns,
                                isTitle = true
                            )
                        },
                        rowClickable = true,
                        trailing = {
                            IconButton(
                                onClick = {
                                    productPriceViewModel.onEvent(ProductPriceEvent.SelectAllProduct)
                                }
                            ) {
                                Icon(imageVector = Icons.Default.Rule, contentDescription = "Select All Product")
                            }
                        },
                        expand = {  modifier: Modifier ->
                            IconButton(
                                modifier = modifier,
                                onClick = {
                                    productsExpanded = !productsExpanded
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    contentDescription = "Expand More",
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        },
                        content = {
                            LazyColumn(
                                state = lazyListState,
                            ){
                                groupedProducts.forEach{ (category, products_new) ->
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
                                            text = category.categoryName,
                                            count = products_new.count(),
                                            onClick = {
                                                productPriceViewModel.onEvent(ProductPriceEvent.SelectProducts(products_new.map { it.productId }))
                                            }
                                        )
                                    }

                                    itemsIndexed(
                                        items = products_new,
                                    ){ index, product ->
                                        ProductCard(
                                            productName = product.productName,
                                            productPrice = product.productPrice.toString(),
                                            productCategoryName = product.category.categoryName,
                                            doesSelected = selectedProducts.contains(product.productId),
                                            isAvailable = product.productAvailability,
                                            onSelectProduct = {
                                                productPriceViewModel.onEvent(ProductPriceEvent.SelectProduct(product.productId))
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
                    )
                }

                Spacer(modifier = Modifier.height(SpaceMedium))
            }

            StandardOutlinedTextField(
                modifier = Modifier,
                text = productPriceViewModel.productPrice.value.productPrice,
                hint = "Product Price",
                keyboardType = KeyboardType.Number,
                error = productPriceViewModel.productPrice.value.productPriceError,
                onValueChange = {
                    productPriceViewModel.onEvent(ProductPriceEvent.OnPriceChanged(it))
                },
            )

            Spacer(modifier = Modifier.height(SpaceMedium))

            Button(
                onClick = {
                    if (type == "Increase") {
                        productPriceViewModel.onEvent(ProductPriceEvent.IncreaseProductPrice)
                    } else {
                        productPriceViewModel.onEvent(ProductPriceEvent.DecreaseProductPrice)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(ButtonSize),
            ) {
                Text(
                    text = if (type == "Increase")
                        stringResource(id = R.string.increase_product_price).uppercase()
                    else
                        stringResource(id = R.string.decrease_product_price).uppercase(),

                    style = MaterialTheme.typography.button,
                )
            }
        }
    }
}