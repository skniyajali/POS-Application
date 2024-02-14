package com.niyaj.feature.address.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhoneAndroid
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
import androidx.compose.ui.util.trace
import com.niyaj.common.utils.toPrettyDate
import com.niyaj.common.utils.toRupee
import com.niyaj.common.utils.toTime
import com.niyaj.designsystem.theme.LightColor6
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.model.AddressWiseOrder
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardExpandable
import com.niyaj.ui.components.StandardRoundedFilterChip
import com.niyaj.ui.components.TextWithCount
import com.niyaj.ui.components.TextWithIcon
import com.niyaj.ui.event.UiState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecentOrders(
    orderDetailsState: UiState<List<AddressWiseOrder>>,
    onExpanded: () -> Unit,
    doesExpanded: Boolean,
    onClickOrder: (String) -> Unit,
) = trace("Address::RecentOrders") {
    Card(
        onClick = onExpanded,
        modifier = Modifier
            .testTag("EmployeeDetails")
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        backgroundColor = MaterialTheme.colors.background
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
                    targetState = orderDetailsState,
                    label = "Recent Orders State"
                ) { orders ->
                    when (orders) {
                        is UiState.Loading -> LoadingIndicator()

                        is UiState.Empty -> {
                            ItemNotAvailable(text = "No orders made using this address.")
                        }

                        is UiState.Success -> {
                            val groupedByDate = remember {
                                orders.data.groupBy { it.updatedAt.toPrettyDate() }
                            }

                            Column {
                                groupedByDate.forEach { (date, orders) ->
                                    TextWithCount(
                                        modifier = Modifier.background(Color.Transparent),
                                        text = date,
                                        count = orders.size,
                                    )

                                    val groupByCustomer = orders.groupBy { it.customerPhone }

                                    groupByCustomer.forEach { (customerPhone, orderDetails) ->
                                        if (orderDetails.size > 1) {
                                            GroupedOrders(
                                                customerPhone = customerPhone,
                                                orderDetails = orderDetails,
                                                onClickOrder = onClickOrder
                                            )
                                        } else {
                                            ListOfOrders(
                                                orderSize = orders.size,
                                                orderDetails = orderDetails,
                                                onClickOrder = onClickOrder
                                            )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GroupedOrders(
    customerPhone: String,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (String) -> Unit,
) = trace("Address::GroupedOrders") {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextWithIcon(
                text = customerPhone,
                icon = Icons.Default.PhoneAndroid
            )

            val startDate = orderDetails.first().updatedAt
            val endDate = orderDetails.last().updatedAt

            Row(
                modifier = Modifier
                    .padding(SpaceMini),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = endDate.toTime,
                    style = MaterialTheme.typography.body2,
                )

                Spacer(modifier = Modifier.width(SpaceMini))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = "DatePeriod"
                )
                Spacer(modifier = Modifier.width(SpaceMini))
                Text(
                    text = startDate.toTime,
                    style = MaterialTheme.typography.body2,
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
        Divider(modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(SpaceMini))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.Center,
            maxItemsInEachRow = 2,
        ) {
            orderDetails.forEach { order ->
                StandardRoundedFilterChip(
                    text = order.totalPrice.toRupee,
                    icon = Icons.Default.Tag,
                    onClick = {
                        onClickOrder(order.orderId)
                    }
                )

                Spacer(modifier = Modifier.width(SpaceSmall))
            }
        }

        Spacer(modifier = Modifier.height(SpaceMini))
        Divider(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ListOfOrders(
    orderSize: Int,
    orderDetails: List<AddressWiseOrder>,
    onClickOrder: (String) -> Unit,
) = trace("Address::ListOfOrder") {
    orderDetails.forEachIndexed { index, order ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClickOrder(order.orderId)
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
                Text(
                    text = order.customerPhone,
                    textAlign = TextAlign.Start
                )

                order.customerName?.let {
                    Spacer(modifier = Modifier.height(SpaceMini))
                    Text(text = it)
                }
            }

            Text(
                text = order.totalPrice.toRupee,
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = order.updatedAt.toTime,
                textAlign = TextAlign.End
            )
        }

        if (index != orderSize - 1) {
            Spacer(modifier = Modifier.height(SpaceMini))
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(SpaceMini))
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun RecentOrders(
    recentOrders: Map<String, List<AddressWiseOrder>>,
    isLoading: Boolean = false,
    error: String? = null,
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
                if (isLoading) {
                    LoadingIndicator()
                } else if (recentOrders.isEmpty() || error != null) {
                    ItemNotAvailable(text = error ?: "No orders made using this address.")
                } else {
                    Column {
                        recentOrders.forEach { (date, orders) ->
                            TextWithCount(
                                modifier = Modifier.background(Color.Transparent),
                                text = date,
                                count = orders.size,
                            )

                            val groupByCustomer = orders.groupBy { it.customerPhone }

                            groupByCustomer.forEach { (t, u) ->
                                if (u.size > 1) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(SpaceSmall),
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextWithIcon(
                                                text = t,
                                                icon = Icons.Default.PhoneAndroid
                                            )

                                            val startDate = u.first().updatedAt
                                            val endDate = u.last().updatedAt

                                            Row(
                                                modifier = Modifier
                                                    .padding(SpaceMini),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = endDate.toTime,
                                                    style = MaterialTheme.typography.body2,
                                                )

                                                Spacer(modifier = Modifier.width(SpaceMini))
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                                                    contentDescription = "DatePeriod"
                                                )
                                                Spacer(modifier = Modifier.width(SpaceMini))
                                                Text(
                                                    text = startDate.toTime,
                                                    style = MaterialTheme.typography.body2,
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                        Spacer(modifier = Modifier.height(SpaceMini))

                                        FlowRow(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(SpaceSmall),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            u.forEach { order ->
                                                Card(
                                                    backgroundColor = LightColor6,
                                                    modifier = Modifier
                                                        .clickable {
                                                            onClickOrder(order.cartOrderId)
                                                        }
                                                        .testTag(order.cartOrderId)
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        TextWithIcon(
                                                            text = order.orderId,
                                                            icon = Icons.Default.Tag
                                                        )
                                                        Spacer(modifier = Modifier.width(SpaceMini))
                                                        Text(
                                                            text = order.totalPrice.toRupee,
                                                            style = MaterialTheme.typography.body1,
                                                            textAlign = TextAlign.Start,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier
                                                                .padding(SpaceSmall)
                                                        )
                                                    }

                                                }
                                                Spacer(modifier = Modifier.width(SpaceSmall))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(SpaceMini))
                                        Divider(modifier = Modifier.fillMaxWidth())
                                    }
                                } else {
                                    u.forEachIndexed { index, order ->
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
                                                Text(
                                                    text = order.customerPhone,
                                                    textAlign = TextAlign.Start
                                                )

                                                order.customerName?.let {
                                                    Spacer(modifier = Modifier.height(SpaceMini))
                                                    Text(text = it)
                                                }
                                            }

                                            Text(
                                                text = order.totalPrice.toRupee,
                                                textAlign = TextAlign.Start
                                            )

                                            Text(
                                                text = order.updatedAt.toTime,
                                                textAlign = TextAlign.End
                                            )
                                        }

                                        if (index != orders.size - 1) {
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
        )
    }
}