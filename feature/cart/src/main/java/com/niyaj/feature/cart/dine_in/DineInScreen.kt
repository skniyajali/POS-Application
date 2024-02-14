package com.niyaj.feature.cart.dine_in

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.core.ui.R
import com.niyaj.feature.cart.components.CartFooterPlaceOrder
import com.niyaj.feature.cart.components.CartItems
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.flow.collectLatest

/**
 * Dine In Screen
 * @author <a href="https://github.com/niyajali">Sk Niyaj Ali</a
 * @param navController
 * @param scaffoldState
 * @param dineInViewModel
 * @see ResultRecipient
 * @see DineInViewModel
 */
@Composable
fun DineInScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    onClickEditOrder: (String) -> Unit,
    onClickViewOrder: (String) -> Unit,
    dineInViewModel: DineInViewModel = hiltViewModel(),
) {
    val listState = rememberLazyListState()

    val uiState = dineInViewModel.dineInOrders.collectAsStateWithLifecycle().value

    val selectedDineInOrder = dineInViewModel.selectedItems.toList()
    val countSelectedDineInItem = selectedDineInOrder.size

    val addOnItems = dineInViewModel.addOnItems.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        dineInViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        navController.navigate(Screens.ORDER_SCREEN)
                    }
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Crossfade(
        targetState = uiState,
        label = "DineIn Cart::State"
    ) { state ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()

            is UiState.Empty -> {
                ItemNotAvailable(
                    text = stringResource(id = R.string.dine_in_orders_not_found),
                    buttonText = stringResource(id = R.string.add_items_to_cart_button),
                    image = R.drawable.emptycart,
                    onClick = {
                        navController.navigate(Screens.HOME_SCREEN)
                    }
                )
            }

            is UiState.Success -> {
                StandardScaffold(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    showBottomBar = true,
                    showTopBar = false,
                    bottomBar = {
                        if (state.data.isNotEmpty()) {
                            CartFooterPlaceOrder(
                                countTotalItems = state.data.size,
                                countSelectedItem = countSelectedDineInItem,
                                showPrintBtn = false,
                                onClickSelectAll = dineInViewModel::selectAllItems,
                                onClickPlaceAllOrder = {
                                    dineInViewModel.onEvent(DineInEvent.PlaceAllDineInOrder)
                                },
                                onClickPrintAllOrder = {}
                            )
                        }
                    }
                ) {
                    CartItems(
                        listState = listState,
                        cartItems = state.data,
                        selectedCartItems = selectedDineInOrder,
                        addOnItems = addOnItems,
                        onSelectCartOrder = dineInViewModel::selectItem,
                        onClickEditOrder = onClickEditOrder,
                        onClickViewOrder = onClickViewOrder,
                        onClickDecreaseQty = { cartOrderId, productId ->
                            dineInViewModel.onEvent(
                                DineInEvent.DecreaseQuantity(cartOrderId, productId)
                            )
                        },
                        onClickIncreaseQty = { cartOrderId, productId ->
                            dineInViewModel.onEvent(
                                DineInEvent.IncreaseQuantity(cartOrderId, productId)
                            )
                        },
                        onClickAddOnItem = { addOnItemId, cartOrderId ->
                            dineInViewModel.onEvent(
                                DineInEvent.UpdateAddOnItemInCart(addOnItemId, cartOrderId)
                            )
                        },
                        onClickPlaceOrder = {
                            dineInViewModel.onEvent(DineInEvent.PlaceDineInOrder(it))
                        },
                    )
                }
            }
        }
    }
}