package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.GlowIndicator
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.destinations.ProductScreenDestination
import com.niyaj.popos.features.main_feed.domain.model.ProductWithFlowQuantity
import com.niyaj.popos.features.main_feed.presentation.components.category.CategorySection
import com.niyaj.popos.features.main_feed.presentation.components.product.ProductSection
import com.ramcosta.composedestinations.navigation.navigate

/**
 * Main Feed Screen Content
 * @author Sk Niyaj Ali
 */
@Composable
fun FrontLayerContent(
    modifier : Modifier = Modifier,
    navController : NavController,
    lazyListState: LazyListState,
    categoryLazyListState: LazyListState,
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
                .padding(SpaceSmall))
        {
            if(productsHasError != null){
                ItemNotAvailable(
                    text = productsHasError,
                    image = painterResource(id = R.drawable.emptystatetwo),
                )
            } else if(categoriesHasError != null){
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
                    onNavigateToProductScreen = { navController.navigate(ProductScreenDestination) }
                )

                Spacer(modifier = Modifier.height(SpaceSmall))
            }
        }
    }
}