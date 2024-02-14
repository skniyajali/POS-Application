package com.niyaj.feature.product.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Tag
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.theme.Pewter
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.ProductOrder
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductOrderDetails(
    orderState: UiState<List<ProductOrder>>,
    productPrice: Int,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (String) -> Unit,
) {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = SpaceMini
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = doesExpanded,
            onExpandChanged = {
                onExpanded()
            },
            title = {
                TextWithIcon(
                    text = "Recent Orders",
                    icon = Icons.Default.AllInbox,
                    isTitle = true
                )
            },
            rowClickable = true,
            expand = { modifier: Modifier ->
                IconButton(
                    modifier = modifier,
                    onClick = onExpanded
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Expand More",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            },
            content = {
                Crossfade(
                    targetState = orderState,
                    label = ""
                ) { state ->
                    when (state) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(text = "Have not placed any order on this product.")
                        }

                        is UiState.Success -> {
                            val groupedByDate = remember(state.data) {
                                state.data.groupBy { it.orderedDate.toPrettyDate() }
                            }

                            Column {
                                groupedByDate.forEach { (date, orders) ->
                                    val totalSales = orders
                                        .sumOf { it.quantity }
                                        .times(productPrice).toString()

                                    TextWithCount(
                                        modifier = Modifier
                                            .background(Color.Transparent),
                                        text = date,
                                        trailingText = totalSales.toRupee,
                                        count = orders.size,
                                    )

                                    val grpByOrderType = orders.groupBy { it.orderType }

                                    grpByOrderType.forEach { (orderType, grpOrders) ->
                                        val totalPrice = grpOrders
                                            .sumOf { it.quantity }
                                            .times(productPrice).toString()

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Pewter)
                                                .align(Alignment.CenterHorizontally)
                                        ) {
                                            Text(
                                                text = "$orderType - ${totalPrice.toRupee}",
                                                style = MaterialTheme.typography.caption,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .padding(SpaceMini)
                                                    .align(Alignment.Center)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        grpOrders.forEachIndexed { index, order ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        onClickOrder(order.cartOrderId)
                                                    }
                                                    .padding(SpaceSmall),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                TextWithIcon(
                                                    text = order.orderId,
                                                    icon = Icons.Default.Tag,
                                                    isTitle = true,
                                                )

                                                Column {
                                                    order.customerPhone?.let {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Text(text = it)
                                                    }
                                                    Spacer(modifier = Modifier.height(SpaceMini))

                                                    order.customerAddress?.let {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Text(text = it)
                                                    }
                                                }

                                                Text(
                                                    text = "${order.quantity} Qty",
                                                    textAlign = TextAlign.Start,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Text(
                                                    text = order.orderedDate.toTime,
                                                    textAlign = TextAlign.End
                                                )
                                            }

                                            if (index != state.data.size - 1) {
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                                Divider(modifier = Modifier.fillMaxWidth())
                                                Spacer(modifier = Modifier.height(SpaceMini))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}