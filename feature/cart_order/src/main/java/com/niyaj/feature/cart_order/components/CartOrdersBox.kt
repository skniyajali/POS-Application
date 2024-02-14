package com.niyaj.feature.cart_order.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tag
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.ui.components.CircularBox

/**
 * [CartOrder] Box
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CartOrdersBox(
    modifier: Modifier = Modifier,
    item: CartOrder,
    doesSelected: (String) -> Boolean,
    orderSelected: (String) -> Boolean,
    onClickItem: (String) -> Unit,
    onLongClickItem: (String) -> Unit,
    border: BorderStroke = BorderStroke(1.dp, MaterialTheme.colors.primary)
) {
    val borderStroke = if (doesSelected(item.cartOrderId)) border else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
            .combinedClickable(
                enabled = true,
                onClick = {
                    onClickItem(item.cartOrderId)
                },
                onLongClick = {
                    onLongClickItem(item.cartOrderId)
                }
            ),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.surface,
        border = borderStroke,
        elevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularBox(
                modifier = Modifier.padding(end = SpaceSmall),
                icon = Icons.Default.Tag,
                doesSelected = doesSelected(item.cartOrderId),
                showBorder = orderSelected(item.cartOrderId),
                backgroundColor = MaterialTheme.colors.background
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        buildAnnotatedString {
                            if (item.orderType == OrderType.DineOut) {
                                withStyle(
                                    style = SpanStyle(
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                ) {
                                    append(item.address?.shortName?.uppercase())

                                    append(" - ")
                                }
                            }

                            append(item.orderId)
                        },
                        style = MaterialTheme.typography.body1,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    Text(
                        text = item.orderType.name,
                        style = MaterialTheme.typography.body2
                    )
                }

                if (item.cartOrderStatus == OrderStatus.PLACED) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colors.secondaryVariant,
                                RoundedCornerShape(SpaceMini)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            modifier = Modifier.padding(SpaceMini),
                            text = item.cartOrderStatus.name,
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }
}