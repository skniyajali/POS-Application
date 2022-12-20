package com.niyaj.popos.features.cart.presentation.dine_in

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.addon_item.presentation.AddOnItemViewModel
import com.niyaj.popos.features.cart.presentation.components.CartAddOnItems
import com.niyaj.popos.features.cart.presentation.components.CartFooterPlaceOrder
import com.niyaj.popos.features.cart.presentation.components.CartItemOrderDetailsSection
import com.niyaj.popos.features.cart.presentation.components.CartItemProductDetailsSection
import com.niyaj.popos.features.cart.presentation.components.CartItemTotalPriceSection
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DineInScreen(
    navController: NavController,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    dineInViewModel: DineInViewModel = hiltViewModel(),
    addOnItemViewModel: AddOnItemViewModel = hiltViewModel(),
) {

    val listState = rememberLazyListState()

    val dineInOrders = dineInViewModel.dineInOrders.collectAsState().value.cartItems
    val isLoading = dineInViewModel.dineInOrders.collectAsState().value.isLoading
    val hasError = dineInViewModel.dineInOrders.collectAsState().value.error

    val countTotalDineInItems = dineInViewModel.dineInOrders.collectAsState().value.cartItems.size
    val selectedDineInOrder = dineInViewModel.selectedDineInOrder.collectAsState().value
    val countSelectedDineInItem = selectedDineInOrder.size

    val addOnItems = addOnItemViewModel.state.collectAsState().value.addOnItems

    LaunchedEffect(key1 = true){
        dineInViewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.OnSuccess -> {
                    val result = bottomSheetScaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        navController.navigate(OrderScreenDestination())
                    }
                }

                is UiEvent.OnError -> {
                    bottomSheetScaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isLoading),
        onRefresh = {
            dineInViewModel.onDineInEvent(DineInEvent.RefreshDineInOrder)
        }
    ) {
        if(isLoading){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        } else if(countTotalDineInItems == 0 || hasError != null) {
            ItemNotAvailable(
                text = hasError ?: stringResource(id = R.string.dine_in_orders_not_found),
                buttonText = stringResource(id = R.string.add_items_to_cart_button),
                onClick = {
                    navController.navigate(MainFeedScreenDestination())
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.7f, true)
                        .padding(SpaceSmall),
                    verticalArrangement = Arrangement.Top,
                    state = listState,
                ) {
                    items(dineInOrders){ cartItem ->
                        if(cartItem.cartOrder != null && cartItem.cartProducts.isNotEmpty()) {

                            val newOrderId = if(!cartItem.cartOrder.address?.shortName.isNullOrEmpty()){
                                cartItem.cartOrder.address?.shortName?.uppercase().plus(" -")
                                    .plus(cartItem.cartOrder.orderId)
                            }else{
                                cartItem.cartOrder.orderId
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.surface,
                                        RoundedCornerShape(6.dp))
                                    .clickable {
                                        dineInViewModel.onDineInEvent(
                                            DineInEvent.SelectDineInOrder(
                                                cartItem.cartOrder.cartOrderId
                                            )
                                        )
                                    },
                                elevation = 6.dp
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                ) {
                                    CartItemOrderDetailsSection(
                                        orderId = newOrderId,
                                        orderType =  cartItem.cartOrder.orderType,
                                        customerPhone = cartItem.cartOrder.customer?.customerPhone,
                                        selected = selectedDineInOrder.contains(cartItem.cartOrder.cartOrderId),
                                        onClick = {
                                            dineInViewModel.onDineInEvent(
                                                DineInEvent.SelectDineInOrder(
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                        },
                                        onEditClick = {
                                            navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = cartItem.cartOrder.cartOrderId))
                                        },
                                        onViewClick = {
//                                        cartOrderId= cartItem.cartOrder.cartOrderId
                                            navController.navigate(OrderDetailsScreenDestination())
                                        }
                                    )

                                    CartItemProductDetailsSection(
                                        cartProducts = cartItem.cartProducts,
                                        decreaseQuantity = {
                                            dineInViewModel.onDineInEvent(
                                                DineInEvent.RemoveProductFromCart(
                                                    cartItem.cartOrder.cartOrderId,
                                                    it
                                                )
                                            )
                                        },
                                        increaseQuantity = {
                                            dineInViewModel.onDineInEvent(
                                                DineInEvent.AddProductToCart(
                                                    cartItem.cartOrder.cartOrderId,
                                                    it
                                                )
                                            )
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(SpaceMedium))

                                    val selectedAddOnItems = cartItem.cartOrder.addOnItems.map {
                                        it.addOnItemId
                                    }

                                    CartAddOnItems(
                                        addOnItems = addOnItems,
                                        selectedAddOnItem = selectedAddOnItems,
                                        onClick = {
                                            dineInViewModel.onDineInEvent(
                                                DineInEvent.UpdateAddOnItemInCart(
                                                    it,
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                        },
                                    )

                                    Spacer(modifier = Modifier.height(SpaceMedium))

                                    CartItemTotalPriceSection(
                                        itemCount = cartItem.cartProducts.size,
                                        totalPrice = cartItem.orderPrice.first,
                                        discountPrice = cartItem.orderPrice.second,
                                        showPrintBtn = false,
                                        onClickPlaceOrder = {
                                            dineInViewModel.onDineInEvent(
                                                DineInEvent.PlaceDineInOrder(
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                        },
                                        onClickPrintOrder = {}
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }

                CartFooterPlaceOrder(
                    modifier = Modifier.weight(0.3f, true),
                    countTotalItems = countTotalDineInItems,
                    countSelectedItem = countSelectedDineInItem,
                    showPrintBtn = false,
                    onClickSelectAll = {
                        dineInViewModel.onDineInEvent(DineInEvent.SelectAllDineInOrder)
                    },
                    onClickPlaceOrder = {
                        dineInViewModel.onDineInEvent(DineInEvent.PlaceAllDineInOrder)
                    },
                    onClickPrintOrder = {}
                )
            }
        }
    }
}