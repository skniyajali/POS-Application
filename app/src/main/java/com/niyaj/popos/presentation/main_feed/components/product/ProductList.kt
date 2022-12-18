package com.niyaj.popos.presentation.main_feed.components.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceSmall

@Composable
fun ProductList(
    recipes: LazyPagingItems<ProductWithQuantity>,
) {
    LazyColumn {
        item {
            Text(
                text = "Scroll for more recipes!",
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(SpaceMedium))
        }
        when (val state = recipes.loadState.prepend) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        text = state.error.message ?: "",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
        when (val state = recipes.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        text = state.error.message ?: "",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
        items(
            items = recipes,
            key = { it.product.productId }
        ) {
            RecipeRow(recipeModel = it)
        }

        when (val state = recipes.loadState.append) {
            is LoadState.NotLoading -> Unit
            is LoadState.Loading -> {
                item {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
            }
            is LoadState.Error -> {
                item {
                    Text(
                        text = state.error.message ?: "",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }
}


@Composable
private fun RecipeRow(
    recipeModel: ProductWithQuantity?,
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = recipeModel?.product?.productName ?: "",
                    modifier = Modifier
                        .placeholder(
                            visible = recipeModel == null,
                            highlight = PlaceholderHighlight.fade(),
                        )
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(SpaceSmall))
                Text(
                    text = recipeModel?.quantity.toString(),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .placeholder(
                            visible = recipeModel == null,
                            highlight = PlaceholderHighlight.fade(),
                        )
                        .fillMaxWidth()
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(SpaceSmall))
}