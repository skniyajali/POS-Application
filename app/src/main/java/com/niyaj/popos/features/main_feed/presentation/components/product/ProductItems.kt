package com.niyaj.popos.features.main_feed.presentation.components.product

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.niyaj.popos.features.common.ui.theme.Cream
import com.niyaj.popos.features.common.ui.theme.IconSizeMedium
import com.niyaj.popos.features.common.ui.theme.LightColor9
import com.niyaj.popos.features.common.ui.theme.PoposPink100
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.main_feed.data.repository.ProductWithFlowQuantity
import com.niyaj.popos.util.toRupee

@Composable
fun ProductItems(
    cartProducts: List<ProductWithFlowQuantity>,
    onLeftClick: (String) -> Unit = {},
    onRightClick: (String) -> Unit = {},
    isLoading: Boolean = false,
){
    LazyColumn{
        itemsIndexed(cartProducts){ index, productWithQuantity ->
            val quantity = productWithQuantity.quantity.collectAsState(0).value

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 1.dp,
                shape =  RoundedCornerShape(4.dp),
                backgroundColor = MaterialTheme.colors.surface,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1.5f)
                            .placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.shimmer(),
                                color = Cream,
                            )
                            .clickable(
                                enabled = quantity != 0
                            ) {
                                onLeftClick(productWithQuantity.product.productId)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ){
                            Column(
                                modifier = Modifier
                                    .placeholder(
                                        visible = isLoading,
                                        highlight = PlaceholderHighlight.fade(),
                                    )
                                    .padding(SpaceSmall),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center
                            ){
                                Text(
                                    text = productWithQuantity.product.productName,
                                    style = MaterialTheme.typography.body1,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = productWithQuantity.product.productPrice.toString().toRupee,
                                    style = MaterialTheme.typography.subtitle2,
                                    color = Color.Black
                                )
                            }

                            if(quantity != 0) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(IconSizeMedium)
                                )
                            }
                        }
                    }

                    Divider(
                        color = PoposPink100,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )

                    Row(
                        modifier = Modifier
                            .clickable {
                                onRightClick(productWithQuantity.product.productId)
                            }
                            .weight(1.5f)
                            .fillMaxHeight()
                            .placeholder(
                                visible = isLoading,
                                highlight = PlaceholderHighlight.shimmer(),
                                color = LightColor9,
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if(quantity != 0) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            Text(
                                text = quantity.toString(),
                                style = MaterialTheme.typography.h4,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colors.secondaryVariant
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Default.AddShoppingCart,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(IconSizeMedium)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            if(index == cartProducts.size - 1) {
                Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
            }
        }
    }
}
