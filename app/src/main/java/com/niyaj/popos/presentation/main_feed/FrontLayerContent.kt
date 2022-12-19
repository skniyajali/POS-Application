package com.niyaj.popos.presentation.main_feed

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
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.main_feed.components.category.CategorySection
import com.niyaj.popos.presentation.main_feed.components.product.ProductSection
import com.niyaj.popos.presentation.main_feed.components.product.ProductWithQuantity
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

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