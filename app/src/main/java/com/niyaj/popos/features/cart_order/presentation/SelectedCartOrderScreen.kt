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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.Teal200
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.util.BottomSheetWithCloseDialog
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.SelectedCartOrderScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationStyle
import io.sentry.compose.SentryTraced

/**
 * Select Cart Order Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param cartOrderViewModel
 * @see CartOrderViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Destination(style = DestinationStyle.BottomSheet::class)
@Composable
fun SelectedCartOrderScreen(
    navController: NavController = rememberNavController(),
    cartOrderViewModel: CartOrderViewModel = hiltViewModel(),
) {
    val allOrders = cartOrderViewModel.cartOrders.collectAsState().value.cartOrders
    val isLoading = cartOrderViewModel.cartOrders.collectAsState().value.isLoading
    val hasError = cartOrderViewModel.cartOrders.collectAsState().value.error

    val selectedCartOrder = cartOrderViewModel.selectedCartOrder.collectAsState().value
    val selectedOrderId = selectedCartOrder.cartOrderId

    SentryTraced(tag = SelectedCartOrderScreenDestination.route) {
        BottomSheetWithCloseDialog(
            modifier = Modifier.fillMaxSize(),
            text = "Select Cart Order",
            onClosePressed = {
                navController.navigateUp()
            }
        ){
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = isLoading),
                onRefresh = {
                    cartOrderViewModel.onEvent(CartOrderEvent.RefreshCartOrder)
                }
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    if (isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
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
                                CartOrderBox(
                                    cartOrder = item,
                                    selectedOrderId = selectedOrderId,
                                    onClickCartOrder = {
                                        cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrderEvent(it))
                                        navController.navigateUp()
                                    },
                                    onClickEdit = {
                                        cartOrderViewModel.onEvent(CartOrderEvent.SelectCartOrderEvent(it))
                                        navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = it))
                                        navController.navigateUp()
                                    },
                                    onClickDelete = {
                                        cartOrderViewModel.onEvent(CartOrderEvent.DeleteCartOrder(it))
                                        navController.navigateUp()
                                    }
                                )

                                Spacer(modifier = Modifier.height(SpaceMedium))
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Cart Order Box Composable
 */
@Composable
fun CartOrderBox(
    cartOrder: CartOrder,
    selectedOrderId: String,
    onClickCartOrder: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    selectedColor : Color = MaterialTheme.colors.secondary,
    unselectedColor : Color = Teal200,
) {
    val color = if (selectedOrderId == cartOrder.cartOrderId) selectedColor else unselectedColor
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
                    onClickCartOrder(cartOrder.cartOrderId)
                }
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(buildAnnotatedString {
                if (!cartOrder.address?.addressName.isNullOrEmpty()) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        cartOrder.address?.shortName?.let { append(it.uppercase()) }
                        append(" - ")
                    }
                }
                append(cartOrder.orderId)
            })

            Spacer(modifier = Modifier.width(SpaceMedium))
            Icon(
                imageVector = if (selectedOrderId == cartOrder.cartOrderId)
                    Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = color
            )
        }
        Spacer(modifier = Modifier.width(SpaceSmall))
        IconButton(
            onClick = {
                onClickEdit(cartOrder.cartOrderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colors.primary, RoundedCornerShape(4.dp))
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
                onClickDelete(cartOrder.cartOrderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colors.error, RoundedCornerShape(4.dp))
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
}