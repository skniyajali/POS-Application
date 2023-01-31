package com.niyaj.popos.features.main_feed.presentation.components.product

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.main_feed.data.repository.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.presentation.components.components.TitleWithIcon

@Composable
fun ProductSection(
    onProductFilterClick: () -> Unit = {},
    products: List<ProductWithFlowQuantity> = emptyList(),
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
    isLoading: Boolean = false,
) {
    TitleWithIcon(
        text = "Products",
        icon = Icons.Default.Dns,
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
            },
            isLoading = isLoading,
        )
    }else {
        Text(
            text = stringResource(id = R.string.no_items_in_product),
            color = TextGray
        )
    }
}