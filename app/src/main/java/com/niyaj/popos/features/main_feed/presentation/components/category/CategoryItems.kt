package com.niyaj.popos.features.main_feed.presentation.components.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.common.ui.theme.SpaceSmall

@Composable
fun CategoryItems(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCategory: String = "",
    selectedColor: Color = MaterialTheme.colors.primary,
    unselectedColor: Color = MaterialTheme.colors.onPrimary,
    isLoading: Boolean = false,
    onClickCategory: (String) -> Unit = {},
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
    ){
        items(categories){ category ->
            CategoryItem(
                category = category,
                selectedCategory = selectedCategory,
                isLoading = isLoading,
                selectedColor = selectedColor,
                unselectedColor = unselectedColor,
                onClickCategory = {
                    onClickCategory(it)
                }
            )

            Spacer(modifier = Modifier.width(SpaceSmall))
        }
    }
}

@Composable
fun CategoryItem(
    category : Category,
    selectedCategory : String,
    isLoading : Boolean,
    selectedColor : Color,
    unselectedColor : Color,
    onClickCategory : (String) -> Unit
) {
    val backgroundColor = if (selectedCategory == category.categoryId) selectedColor else unselectedColor
    val borderStroke = if (selectedCategory == category.categoryId) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, MaterialTheme.colors.primary)
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
            )
            .clip(CutCornerShape(4.dp))
            .border(borderStroke, CutCornerShape(4.dp))
            .clickable {
                onClickCategory(category.categoryId)
            }
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.categoryName,
            style = MaterialTheme.typography.body1,
            color = if (selectedCategory == category.categoryId) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}