package com.niyaj.popos.presentation.main_feed.components.category

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.niyaj.popos.R
import com.niyaj.popos.domain.model.Category
import com.niyaj.popos.presentation.main_feed.components.components.TitleWithIcon
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.TextGray

@Composable
fun CategorySection(
    onCategoryFilterClick: () -> Unit = {},
    categories: List<Category> = emptyList(),
    onCategoryClick: (String) -> Unit = {},
    selectedCategory: String = "",
) {
    TitleWithIcon(
        text = "Categories",
        onClick = {
            onCategoryFilterClick()
        }
    )
    
    Spacer(modifier = Modifier.height(SpaceSmall))
    
    if(categories.isNotEmpty()){
        CategoryItems(
            categories = categories,
            selectedCategory = selectedCategory,
            onClick = {
                onCategoryClick(it)
            },
        )
    }else {
        Text(
            text = stringResource(id = R.string.no_items_in_category),
            color = TextGray
        )
    }

}