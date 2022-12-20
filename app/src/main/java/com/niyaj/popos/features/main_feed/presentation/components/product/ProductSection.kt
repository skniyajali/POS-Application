package com.niyaj.popos.features.main_feed.presentation.components.product

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.LazyPagingItems
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.main_feed.presentation.components.components.TitleWithIcon

@Composable
fun ProductSection(
    onProductFilterClick: () -> Unit = {},
    products: List<ProductWithQuantity> = emptyList(),
    pagingProducts: LazyPagingItems<ProductWithQuantity>,
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
) {
    TitleWithIcon(
        text = "Products",
        onClick = {
            onProductFilterClick()
        }
    )

    Spacer(modifier = Modifier.height(SpaceSmall))

    if(products.isNotEmpty()){
        ProductItems(
            cartProducts = products,
            onLeftClick = { product ->
                onProductLeftClick(product)
            },
            onRightClick = { product ->
                onProductRightClick(product)
            }
        )
    }else {
        Text(
            text = stringResource(id = R.string.no_items_in_product),
            color = TextGray
        )
    }


//
//    Spacer(modifier = Modifier.height(SpaceSmall))
//
//    ProductList(pagingProducts)
//
//    Spacer(modifier = Modifier.height(SpaceSmall))
}