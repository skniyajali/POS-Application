package com.niyaj.feature.home.components.category

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.feature.home.components.components.TitleWithIcon
import com.niyaj.model.Category
import com.niyaj.ui.components.CategoryItems

@Composable
fun CategorySection(
    lazyListState: LazyListState,
    categories: List<Category> = emptyList(),
    selectedCategory: String = "",
    onCategoryFilterClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
) {
    if (categories.isNotEmpty()) {
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
            onClickCategory = {
                onCategoryClick(it)
            },
        )
    }
}