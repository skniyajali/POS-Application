package com.niyaj.popos.features.main_feed.presentation.components.category

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.main_feed.presentation.components.components.TitleWithIcon

@Composable
fun CategorySection(
    lazyListState: LazyListState,
    categories: List<Category> = emptyList(),
    selectedCategory: String = "",
    isLoading: Boolean = false,
    onCategoryFilterClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
) {
    if(categories.isNotEmpty()){
        TitleWithIcon(
            text = "Categories",
            icon = Icons.Default.Category,
            onClick = {
                onCategoryFilterClick()
            }
        )

        Spacer(modifier = Modifier.height(SpaceSmall))

        CategoryItems(
            categories = categories,
            lazyListState = lazyListState,
            selectedCategory = selectedCategory,
            isLoading = isLoading,
            onClickCategory = {
                onCategoryClick(it)
            },
        )
    }
}