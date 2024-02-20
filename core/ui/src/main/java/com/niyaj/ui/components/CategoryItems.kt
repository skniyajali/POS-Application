package com.niyaj.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.Category

@Composable
fun CategoryItems(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    categories: List<Category>,
    selectedCategory: String = "",
    selectedColor: Color = MaterialTheme.colors.secondaryVariant,
    unselectedColor: Color = MaterialTheme.colors.onPrimary,
    onClickCategory: (String) -> Unit = {},
) {
    LazyRow(
        state = lazyListState,
        modifier = modifier
            .fillMaxWidth()
    ) {
        items(
            items = categories,
            key = {
                it.categoryId
            }
        ) { category ->
            CategoryItem(
                category = category,
                selectedCategory = selectedCategory,
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
    category: Category,
    selectedCategory: String,
    selectedColor: Color,
    unselectedColor: Color,
    onClickCategory: (String) -> Unit,
) {
    val backgroundColor =
        if (selectedCategory == category.categoryId) selectedColor else unselectedColor
    val borderStroke = if (selectedCategory == category.categoryId) BorderStroke(
        0.dp,
        Color.Transparent
    ) else BorderStroke(1.dp, MaterialTheme.colors.secondaryVariant)
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp)
            .clip(CutCornerShape(4.dp))
            .border(borderStroke, CutCornerShape(4.dp))
            .clickable {
                onClickCategory(category.categoryId)
            }
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceMini),
            horizontalArrangement = Arrangement.spacedBy(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CircularBox(
                icon = Icons.Default.Category,
                doesSelected = selectedCategory == category.categoryId,
                size = 30.dp,
                backgroundColor = Color.Transparent,
                selectedTint = MaterialTheme.colors.onPrimary,
            )

            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.body1,
                color = if (selectedCategory == category.categoryId) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}