package com.niyaj.popos.features.main_feed.presentation.components.category

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.main_feed.presentation.components.components.TitleWithIcon

@Composable
fun CategorySection(
    onCategoryFilterClick: () -> Unit = {},
    categories: List<Category> = emptyList(),
    onCategoryClick: (String) -> Unit = {},
    selectedCategory: String = "",
    isLoading: Boolean = false,
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
            selectedCategory = selectedCategory,
            isLoading = isLoading,
            onClick = {
                onCategoryClick(it)
            },
        )
    }
}