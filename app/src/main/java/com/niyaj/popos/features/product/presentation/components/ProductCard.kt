package com.niyaj.popos.features.product.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Rule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.popos.common.utils.toRupee
import com.niyaj.popos.features.common.ui.theme.LightColor8
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardOutlinedChip
import com.niyaj.popos.features.components.TextWithCount
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.header
import com.niyaj.popos.features.product.domain.model.Product


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductCard(
    modifier : Modifier = Modifier,
    product: Product,
    doesSelected : Boolean = false,
    doesAnySelected : Boolean = false,
    showCategory : Boolean = true,
    onSelectProduct : (String) -> Unit,
    onClickProduct : (String) -> Unit = {},
    backgroundColor : Color = MaterialTheme.colors.onPrimary,
    contentColor : Color = MaterialTheme.colors.onSurface,
) {
    val border = if (doesSelected) BorderStroke(1.dp, MaterialTheme.colors.primary)
    else if (!product.productAvailability) BorderStroke(1.dp, TextGray) else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (!doesAnySelected) {
                        onClickProduct(product.productId)
                    }else {
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

            if (showCategory) {
                StandardOutlinedChip(
                    text = product.category?.categoryName ?: "Uncategorized",
                    onClick = {}
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
    modifier : Modifier = Modifier,
    lazyListState: LazyListState,
    lazyGridState : LazyGridState,
    groupedProducts:  Map<String?, List<Product>>,
    selectedProducts: List<String> = emptyList(),
    showScrollToTop: Boolean = false,
    onCategorySelect: (products: List<String>) -> Unit,
    onSelectProduct: (productId: String) -> Unit,
    productsExpanded : Boolean = false,
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
                    text = if(selectedProductsSize != 0) "$selectedProductsSize Selected" else "Choose Products",
                    icon = Icons.Default.Dns,
                    isTitle = true
                )
            },
            rowClickable = true,
            trailing = {
                IconButton(
                    onClick = onClickSelectAll
                ) {
                    Icon(imageVector = Icons.Default.Rule, contentDescription = "Select All Product")
                }
            },
            expand = {  modifier: Modifier ->
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
                    showScrollToTop = showScrollToTop,
                    onCategorySelect = onCategorySelect,
                    onSelectProduct = onSelectProduct
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
    modifier : Modifier = Modifier,
    lazyListState: LazyListState,
    lazyGridState : LazyGridState,
    groupedProducts:  Map<String?, List<Product>>,
    selectedProducts: List<String>,
    showScrollToTop: Boolean = false,
    viewType : ViewType = ViewType.COLUMN,
    headerColor : Color = LightColor8,
    onCategorySelect: (products: List<String>) -> Unit,
    onSelectProduct: (productId: String) -> Unit,
    onClickProduct: (productId: String) -> Unit = {},
) {
    if (viewType == ViewType.COLUMN) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier,
        ){
            groupedProducts.forEach{ (category, products) ->
                stickyHeader {
                    category?.let { category ->
                        TextWithCount(
                            modifier = Modifier
                                .background(
                                    if (showScrollToTop) headerColor else Color.Transparent
                                )
                                .clip(
                                    RoundedCornerShape(if (showScrollToTop) 4.dp else 0.dp)
                                ),
                            text = category,
                            count = products.size,
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
                ){ _, product ->
                    ProductCard(
                        product = product,
                        doesSelected = selectedProducts.contains(product.productId),
                        doesAnySelected = selectedProducts.isNotEmpty(),
                        onSelectProduct = onSelectProduct,
                        onClickProduct = onClickProduct
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = lazyGridState,
        ) {
            groupedProducts.forEach{ (category, products) ->
                header {
                    category?.let { category ->
                        TextWithCount(
                            modifier = Modifier
                                .background(
                                    if (showScrollToTop) headerColor else Color.Transparent
                                )
                                .clip(
                                    RoundedCornerShape(if (showScrollToTop) 4.dp else 0.dp)
                                ),
                            text = category,
                            count = products.size,
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
                ){ _, product ->
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


enum class ViewType {
    ROW,
    COLUMN,
}