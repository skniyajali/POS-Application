package com.niyaj.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.home.components.category.CategorySection
import com.niyaj.feature.home.components.product.ProductSection
import com.niyaj.model.Category
import com.niyaj.model.ProductWithFlowQuantity
import com.niyaj.ui.components.GlowIndicator
import com.niyaj.ui.components.ItemNotAvailable

/**
 * Main Feed Screen Content
 * @author Sk Niyaj Ali
 */
@Composable
fun FrontLayerContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    categoryLazyListState: LazyListState = rememberLazyListState(),
    categoriesIsLoading: Boolean = false,
    productsIsLoading: Boolean = false,
    productsHasError: String? = null,
    categoriesHasError: String? = null,
    onCategoryFilterClick: () -> Unit = {},
    categories: List<Category> = emptyList(),
    onCategoryClick: (String) -> Unit = {},
    selectedCategory: String = "",
    onProductFilterClick: () -> Unit = {},
    products: List<ProductWithFlowQuantity>,
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
    onRefreshFrontLayer: () -> Unit = {},
    onNavigateToProductScreen: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = categoriesIsLoading || productsIsLoading),
        onRefresh = onRefreshFrontLayer,
        indicator = { state, trigger ->
            GlowIndicator(
                swipeRefreshState = state,
                refreshTriggerDistance = trigger
            )
        },
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(SpaceSmall)
        )
        {
            if (productsHasError != null) {
                ItemNotAvailable(
                    text = productsHasError,
                    image = R.drawable.emptystatetwo,
                )
            } else if (categoriesHasError != null) {
                ItemNotAvailable(
                    text = categoriesHasError,
                )
            } else {
                CategorySection(
                    lazyListState = categoryLazyListState,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryClick = onCategoryClick,
                    onCategoryFilterClick = onCategoryFilterClick,
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                ProductSection(
                    products = products,
                    lazyListState = lazyListState,
                    onProductFilterClick = onProductFilterClick,
                    onProductLeftClick = onProductLeftClick,
                    onProductRightClick = onProductRightClick,
                    isLoading = productsIsLoading || categoriesIsLoading,
                    onNavigateToProductScreen = onNavigateToProductScreen
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}