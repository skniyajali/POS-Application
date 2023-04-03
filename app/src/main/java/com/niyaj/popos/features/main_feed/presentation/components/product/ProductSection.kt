package com.niyaj.popos.features.main_feed.presentation.components.product

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.main_feed.data.repository.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.presentation.components.components.TitleWithIcon
import kotlinx.coroutines.launch

@Composable
fun ProductSection(
    onProductFilterClick: () -> Unit = {},
    products: List<ProductWithFlowQuantity> = emptyList(),
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
    onNavigateToProductScreen: () -> Unit = {},
    isLoading: Boolean = false,
) {
    val lazyListState = rememberLazyListState()

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    val scope = rememberCoroutineScope()

    if(products.isNotEmpty()){
        TitleWithIcon(
            text = "Products",
            icon = Icons.Default.Dns,
            showScrollToTop = showScrollToTop.value,
            onClick = {
                onProductFilterClick()
            },
            onClickScrollToTop = {
                scope.launch {
                    lazyListState.animateScrollToItem(0)
                }
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        ProductItems(
            cartProducts = products,
            onLeftClick = { product ->
                onProductLeftClick(product)
            },
            onRightClick = { product ->
                onProductRightClick(product)
            },
            isLoading = isLoading,
            lazyListState = lazyListState,
        )
    }else {
        ItemNotAvailable(
            text = stringResource(id = R.string.no_items_in_product),
            image = painterResource(id = R.drawable.nothinghere),
            buttonText = stringResource(id = R.string.create_product),
            onClick = onNavigateToProductScreen
        )
    }
}