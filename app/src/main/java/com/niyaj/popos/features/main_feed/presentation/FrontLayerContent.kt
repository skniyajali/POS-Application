package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.main_feed.domain.model.ProductWithQuantity
import com.niyaj.popos.features.main_feed.presentation.components.category.CategorySection
import com.niyaj.popos.features.main_feed.presentation.components.product.ProductSection

@Composable
fun FrontLayerContent(
    categoriesIsLoading: Boolean = false,
    productsIsLoading: Boolean = false,
    productsHasError: String? = null,
    categoriesHasError: String? = null,
    onCategoryFilterClick: () -> Unit = {},
    categories: List<Category> = emptyList(),
    onCategoryClick: (String) -> Unit = {},
    selectedCategory: String = "",
    onProductFilterClick: () -> Unit = {},
    products: List<ProductWithQuantity>,
    pagingProducts: LazyPagingItems<ProductWithQuantity>,
    onProductLeftClick: (String) -> Unit = {},
    onProductRightClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(SpaceSmall))
    {
        if(categoriesIsLoading || productsIsLoading){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        } else if(productsHasError != null){
            ItemNotAvailable(
                text = productsHasError,
            )
        } else if(categoriesHasError != null){
            ItemNotAvailable(
                text = categoriesHasError,
            )
        } else {

            CategorySection(
                categories = categories,
                onCategoryFilterClick = onCategoryFilterClick,
                onCategoryClick = onCategoryClick,
                selectedCategory = selectedCategory,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))

            ProductSection(
                products = products,
                pagingProducts = pagingProducts,
                onProductFilterClick = onProductFilterClick,
                onProductLeftClick = onProductLeftClick,
                onProductRightClick = onProductRightClick,
            )

            Spacer(modifier = Modifier.height(SpaceSmall))
        }
    }
}