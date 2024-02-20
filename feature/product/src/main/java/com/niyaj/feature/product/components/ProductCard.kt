package com.niyaj.feature.product.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.common.tags.ProductTestTags.PRODUCT_TAG
import com.niyaj.common.utils.toRupee
import com.niyaj.designsystem.theme.LightColor8
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.TextGray
import com.niyaj.model.Product
import com.niyaj.ui.components.CircularBox
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardOutlinedChip
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.components.header
import com.niyaj.ui.util.isScrolled

enum class ViewType {
    ROW,
    COLUMN,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    modifier: Modifier = Modifier,
    product: Product,
    doesSelected: Boolean = false,
    doesAnySelected: Boolean = false,
    showCategory: Boolean = true,
    showCircularBox: Boolean = false,
    showBackArrow: Boolean = false,
    onSelectProduct: (String) -> Unit,
    onClickProduct: (String) -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.onPrimary,
    contentColor: Color = MaterialTheme.colors.onSurface,
) {
    val border = if (doesSelected) BorderStroke(1.dp, MaterialTheme.colors.primary)
    else if (!product.productAvailability) BorderStroke(1.dp, TextGray) else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(PRODUCT_TAG.plus(product.productId))
            .combinedClickable(
                onClick = {
                    if (!doesAnySelected) {
                        onClickProduct(product.productId)
                    } else {
                        onSelectProduct(product.productId)
                    }
                },
                onLongClick = {
                    onSelectProduct(product.productId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        border = border,
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceSmall)
            ) {
                AnimatedVisibility(
                    visible = showCircularBox
                ) {
                    CircularBox(
                        icon = Icons.Default.Dns,
                        doesSelected = doesSelected,
                        text = product.productName,
                        showBorder = !product.productAvailability
                    )
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = product.productName,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                    Spacer(modifier = Modifier.height(SpaceMini))
                    Text(
                        text = product.productPrice.toString().toRupee,
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            AnimatedVisibility(visible = showCategory) {
                StandardOutlinedChip(
                    text = product.category?.categoryName ?: "Uncategorized",
                    onClick = {}
                )
            }

            AnimatedVisibility(visible = showBackArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                    contentDescription = "View Details"
                )
            }
        }
    }
}


/**
 *
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductBody(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    groupedProducts: Map<String?, List<Product>>,
    selectedProducts: List<String> = emptyList(),
    onCategorySelect: (products: List<String>) -> Unit,
    onSelectProduct: (productId: String) -> Unit,
    productsExpanded: Boolean = false,
    onExpandChange: () -> Unit,
    selectedProductsSize: Int = 0,
    onClickSelectAll: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onExpandChange()
            },
        shape = RoundedCornerShape(4.dp),
        backgroundColor = LightColor8,
    ) {
        StandardExpandable(
            onExpandChanged = {
                onExpandChange()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = productsExpanded,
            title = {
                TextWithIcon(
                    text = if (selectedProductsSize != 0) "$selectedProductsSize Selected" else "Choose Products",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Rule,
                        contentDescription = "Select All Product"
                    )
                }
            },
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpandChange
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                ProductBodyContent(
                    lazyListState = lazyListState,
                    lazyGridState = lazyGridState,
                    groupedProducts = groupedProducts,
                    selectedProducts = selectedProducts,
                    onCategorySelect = onCategorySelect,
                    onSelectProduct = onSelectProduct,
                    onClickProduct = onSelectProduct,
                    showCategory = true,
                    showBackArrow = false,
                )
            }
        )
    }
}

/**
 *
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductBodyContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    lazyGridState: LazyGridState,
    groupedProducts: Map<String?, List<Product>>,
    selectedProducts: List<String>,
    viewType: ViewType = ViewType.COLUMN,
    headerColor: Color = LightColor8,
    showCategory: Boolean = false,
    showBackArrow: Boolean = true,
    onCategorySelect: (products: List<String>) -> Unit,
    onSelectProduct: (productId: String) -> Unit,
    onClickProduct: (productId: String) -> Unit = {},
) {
    Crossfade(
        targetState = viewType,
        label = "Product::Data"
    ) { type ->
        when (type) {
            ViewType.COLUMN -> {
                LazyColumn(
                    state = lazyListState,
                    modifier = modifier.padding(SpaceMini),
                ) {
                    groupedProducts.forEach { (category, products) ->
                        stickyHeader {
                            category?.let { category ->
                                TextWithCount(
                                    modifier = Modifier
                                        .testTag(category),
                                    backGroundColor = if (lazyListState.isScrolled) headerColor else Color.Transparent,
                                    text = category,
                                    count = products.size,
                                    leadingIcon = Icons.Default.Category,
                                    onClick = {
                                        onCategorySelect(products.map { it.productId })
                                    }
                                )
                            }
                        }

                        itemsIndexed(
                            items = products,
                            key = { index, item ->
                                item.productId.plus(index)
                            }
                        ) { _, product ->
                            ProductCard(
                                product = product,
                                doesSelected = selectedProducts.contains(product.productId),
                                doesAnySelected = selectedProducts.isNotEmpty(),
                                showCircularBox = true,
                                showBackArrow = showBackArrow,
                                showCategory = showCategory,
                                onSelectProduct = onSelectProduct,
                                onClickProduct = onClickProduct
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }

            ViewType.ROW -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = lazyGridState,
                ) {
                    groupedProducts.forEach { (category, products) ->
                        header {
                            category?.let { category ->
                                TextWithCount(
                                    modifier = Modifier
                                        .testTag(category),
                                    backGroundColor = if (lazyListState.isScrolled) headerColor else Color.Transparent,
                                    text = category,
                                    count = products.size,
                                    leadingIcon = Icons.Default.Category,
                                    onClick = {
                                        onCategorySelect(products.map { it.productId })
                                    }
                                )
                            }
                        }

                        itemsIndexed(
                            items = products,
                            key = { index, item ->
                                item.productId.plus(index)
                            }
                        ) { _, product ->
                            ProductCard(
                                modifier = Modifier.padding(SpaceMini),
                                product = product,
                                doesSelected = selectedProducts.contains(product.productId),
                                doesAnySelected = selectedProducts.isNotEmpty(),
                                showCategory = false,
                                onSelectProduct = onSelectProduct,
                                onClickProduct = onClickProduct
                            )

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}