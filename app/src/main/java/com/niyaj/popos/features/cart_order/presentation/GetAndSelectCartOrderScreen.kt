package com.niyaj.popos.features.cart_order.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun GetAndSelectCartOrderScreen(
    onClosePressed: () -> Unit = {},
    navController: NavController = rememberNavController(),
    cartOrderViewModel: CartOrderViewModel = hiltViewModel(),
) {

    val selectedColor: Color = MaterialTheme.colors.secondary
    val unselectedColor: Color = Teal200

    val allOrders = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.cartOrders
    val isLoading = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.isLoading
    val hasError = cartOrderViewModel.cartOrders.collectAsStateWithLifecycle().value.error

    val selectedCartOrder = cartOrderViewModel.selectedCartOrder.collectAsStateWithLifecycle().value
    val selectedOrderId = selectedCartOrder.cartOrderId

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = { cartOrderViewModel.onCartOrderEvent(CartOrderEvent.RefreshCartOrder) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(0.5F),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                }
            } else if (allOrders.isEmpty() || hasError != null) {
                ItemNotAvailable(
                    text = stringResource(id = R.string.cart_order_is_empty),
                    buttonText = stringResource(id = R.string.create_new_order).uppercase(),
                    onClick = {
                        navController.navigate(AddEditCartOrderScreenDestination())
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = SpaceMedium)
                ) {
                    itemsIndexed(allOrders.asReversed()) { _, item ->
                        val color: Color =
                            if (selectedOrderId == item.cartOrderId) selectedColor else unselectedColor
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(2f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, color, RoundedCornerShape(4.dp))
                                    .clickable {
                                        cartOrderViewModel.onCartOrderEvent(
                                            CartOrderEvent.SelectCartOrderEvent(item.cartOrderId)
                                        )
                                        onClosePressed()
                                    }
                                    .padding(SpaceSmall),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(buildAnnotatedString {
                                    if (!item.address?.addressName.isNullOrEmpty()) {
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color.Red,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            item.address?.shortName?.let { append(it.uppercase()) }
                                            append(" - ")
                                        }
                                    }
                                    append(item.orderId)
                                })

                                Spacer(modifier = Modifier.width(SpaceMedium))
                                Icon(
                                    imageVector = if (selectedOrderId == item.cartOrderId)
                                        Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = color
                                )
                            }
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            IconButton(
                                onClick = {
                                    cartOrderViewModel.onCartOrderEvent(
                                        CartOrderEvent.SelectCartOrderEvent(
                                            item.cartOrderId
                                        )
                                    )
                                    navController.navigate(
                                        AddEditCartOrderScreenDestination(
                                            cartOrderId = item.cartOrderId
                                        )
                                    )
                                    onClosePressed()
                                },
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colors.primary,
                                        RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Icon(
                                    contentDescription = stringResource(
                                        id = R.string.edit_cart_order_icon,
                                    ),
                                    imageVector = Icons.Default.Edit,
                                    tint = MaterialTheme.colors.onPrimary,
                                )
                            }
                            Spacer(modifier = Modifier.width(SpaceSmall))
                            IconButton(
                                onClick = {
                                    cartOrderViewModel.onCartOrderEvent(
                                        CartOrderEvent.DeleteCartOrder(
                                            item.cartOrderId
                                        )
                                    )
//                                navController.navigateUp()
                                    onClosePressed()
                                },
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colors.error,
                                        RoundedCornerShape(4.dp)
                                    )
                            ) {
                                Icon(
                                    contentDescription = stringResource(
                                        id = R.string.delete_cart_order_icon,
                                    ),
                                    imageVector = Icons.Default.Delete,
                                    tint = MaterialTheme.colors.onError
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(SpaceMedium))
                    }
                }
            }
        }
    }
}