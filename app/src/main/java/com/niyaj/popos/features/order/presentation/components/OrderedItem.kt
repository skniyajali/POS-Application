package com.niyaj.popos.features.order.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Outbox
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.LightColor12
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.domain.model.DineOutOrder
import com.niyaj.popos.utils.toFormattedTime
import com.ramcosta.composedestinations.navigation.navigate
import de.charlex.compose.RevealSwipe


@Composable
fun DineOutOrderedItemLayout(
    navController : NavController,
    dineOutOrders: List<DineOutOrder>,
    isLoading: Boolean = false,
    error: String? = null,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onMarkedAsDelivered: (String) -> Unit,
    onMarkedAsProcessing: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if(dineOutOrders.isEmpty() || error != null){
            ItemNotAvailable(
                text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_order),
                buttonText = stringResource(id = R.string.add_items_to_cart_button),
                image = painterResource(R.drawable.emptycarttwo),
                onClick = {
                    navController.navigate(MainFeedScreenDestination())
                }
            )
        } else if(isLoading){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ){
                items(dineOutOrders){ dineOutOrder ->
                    OrderedItem(
                        orderId = dineOutOrder.orderId,
                        orderPrice = dineOutOrder.totalAmount,
                        orderDate = dineOutOrder.updatedAt,
                        customerPhone = dineOutOrder.customerPhone,
                        customerAddress = dineOutOrder.customerAddress,
                        onClickPrintOrder = {
                            onClickPrintOrder(dineOutOrder.cartOrderId)
                        },
                        onMarkedAsDelivered = {
                            onMarkedAsDelivered(dineOutOrder.cartOrderId)
                        },
                        onMarkedAsProcessing = {
                            onMarkedAsProcessing(dineOutOrder.cartOrderId)
                        },
                        onClickDelete = {
                            onClickDelete(dineOutOrder.cartOrderId)
                        },
                        onClickViewDetails = {
                            navController.navigate(OrderDetailsScreenDestination(cartOrderId = dineOutOrder.cartOrderId))
                        },
                        onClickEdit = {
                            navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = dineOutOrder.cartOrderId))
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}

@Composable
fun DineInOrderedItemLayout(
    navController : NavController,
    dineInOrders: List<DineInOrder>,
    isLoading: Boolean = false,
    error: String? = null,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    onMarkedAsDelivered: (String) -> Unit,
    onMarkedAsProcessing: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if(dineInOrders.isEmpty() || error != null){
            ItemNotAvailable(
                text = error ?: if(showSearchBar) stringResource(id = R.string.search_item_not_found) else stringResource(id = R.string.no_items_in_order),
                buttonText = stringResource(id = R.string.add_items_to_cart_button),
                image = painterResource(R.drawable.emptycarttwo),
                onClick = {
                    navController.navigate(MainFeedScreenDestination())
                }
            )
        } else if(isLoading){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall)
            ){
                items(dineInOrders){ dineInOrder ->
                    OrderedItem(
                        orderId = dineInOrder.orderId,
                        orderPrice = dineInOrder.totalAmount,
                        orderDate = dineInOrder.updatedAt,
                        customerPhone = null,
                        customerAddress = null,
                        onClickPrintOrder = {
                            onClickPrintOrder(dineInOrder.cartOrderId)
                        },
                        onMarkedAsDelivered = {
                            onMarkedAsDelivered(dineInOrder.cartOrderId)
                        },
                        onMarkedAsProcessing = {
                            onMarkedAsProcessing(dineInOrder.cartOrderId)
                        },
                        onClickDelete = {
                            onClickDelete(dineInOrder.cartOrderId)
                        },
                        onClickViewDetails = {
                            navController.navigate(OrderDetailsScreenDestination(cartOrderId = dineInOrder.cartOrderId))
                        },
                        onClickEdit = {
                            navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = dineInOrder.cartOrderId))
                        },
                    )
                    Spacer(modifier = Modifier.height(SpaceMedium))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OrderedItem(
    orderId: String,
    orderPrice: String,
    orderDate: String? = null,
    customerPhone: String? = null,
    customerAddress: String? = null,
    onClickPrintOrder: () -> Unit = {},
    onMarkedAsDelivered : () -> Unit = {},
    onMarkedAsProcessing: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onClickViewDetails: () -> Unit = {},
    onClickEdit: () -> Unit = {},
) {
    RevealSwipe(
        modifier = Modifier
            .fillMaxWidth(),
        onContentClick = {},
        maxRevealDp = 150.dp,
        hiddenContentStart = {
            IconButton(
                onClick = onMarkedAsProcessing
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }

            Spacer(modifier = Modifier.width(SpaceSmall))

            IconButton(
                onClick = onClickEdit
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = null)
            }
        },
        hiddenContentEnd = {
            IconButton(
                onClick = onMarkedAsDelivered
            ) {
                Icon(
                    imageVector = Icons.Default.Outbox,
                    contentDescription = "Mark as Delivered",
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }

            IconButton(
                onClick = onClickDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete order",
                    modifier = Modifier.padding(horizontal = 25.dp),
                )
            }
        },
        contentColor = MaterialTheme.colors.primary,
        backgroundCardContentColor = LightColor12,
        backgroundCardStartColor = MaterialTheme.colors.primary,
        backgroundCardEndColor = MaterialTheme.colors.error,
        shape = RoundedCornerShape(6.dp),
        backgroundStartActionLabel = "Start",
        backgroundEndActionLabel = "End",
    ) {
        OrderedItemData(
            shape = it,
            orderId = orderId,
            orderPrice = orderPrice,
            orderDate = orderDate,
            customerPhone = customerPhone,
            customerAddress = customerAddress,
            onClickViewDetails = onClickViewDetails,
            onClickPrintOrder = onClickPrintOrder
        )
    }
}

@Composable
fun OrderedItemData(
    shape : Shape,
    orderId: String,
    orderPrice: String,
    orderDate: String? = null,
    customerPhone: String? = null,
    customerAddress: String? = null,
    onClickViewDetails: () -> Unit = {},
    onClickPrintOrder: () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = shape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextWithIcon(
                        text = orderId,
                        icon = Icons.Default.Tag
                    )

                    customerPhone?.let {
                        Spacer(modifier = Modifier.height(SpaceSmall))

                        TextWithIcon(
                            text = customerPhone,
                            icon = Icons.Default.PhoneAndroid
                        )
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))

                    orderDate?.let {
                        TextWithIcon(
                            text = orderDate.toFormattedTime,
                            icon = Icons.Default.AccessTime
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    customerAddress?.let {
                        TextWithIcon(
                            text = customerAddress,
                            icon = Icons.Default.Place
                        )
                        Spacer(modifier = Modifier.height(SpaceSmall))
                    }

                    TextWithIcon(
                        text = orderPrice,
                        icon = Icons.Default.CurrencyRupee
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(
                        onClick = onClickViewDetails
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = stringResource(id = R.string.order_details),
                            tint = MaterialTheme.colors.primary,
                        )
                    }

                    Spacer(modifier = Modifier.width(SpaceMini))

                    IconButton(
                        onClick = onClickPrintOrder
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = stringResource(id = R.string.print_order),
                            tint = MaterialTheme.colors.primary,
                        )
                    }
                }
            }
        }
    }
}