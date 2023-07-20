package com.niyaj.popos.features.cart.presentation.dine_in

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.addon_item.presentation.AddOnItemViewModel
import com.niyaj.popos.features.cart.presentation.components.CartFooterPlaceOrder
import com.niyaj.popos.features.cart.presentation.components.CartItems
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest

/**
 * Dine In Screen
 * @author <a href="https://github.com/niyajali">Sk Niyaj Ali</a
 * @param navController
 * @param scaffoldState
 * @param dineInViewModel
 * @param addOnItemViewModel
 * @param resultRecipient
 * @see ResultRecipient
 * @see DineInViewModel
 * @see AddOnItemViewModel
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DineInScreen(
    navController: NavController,
    scaffoldState : ScaffoldState,
    dineInViewModel: DineInViewModel = hiltViewModel(),
    addOnItemViewModel: AddOnItemViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<AddEditCartOrderScreenDestination, String>
) {
    val listState = rememberLazyListState()

    val dineInOrders = dineInViewModel.dineInOrders.collectAsStateWithLifecycle().value.cartItems
    val isLoading = dineInViewModel.dineInOrders.collectAsStateWithLifecycle().value.isLoading
    val hasError = dineInViewModel.dineInOrders.collectAsStateWithLifecycle().value.error

    val countTotalDineInItems = dineInOrders.size
    val selectedDineInOrder = remember {
        dineInViewModel.selectedDineInOrder
    }
    val countSelectedDineInItem = selectedDineInOrder.size

    val addOnItems = addOnItemViewModel.state.collectAsStateWithLifecycle().value.addOnItems

    LaunchedEffect(key1 = true){
        dineInViewModel.eventFlow.collectLatest { event ->
            when(event){
                is UiEvent.Success -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        navController.navigate(OrderScreenDestination())
                    }
                }

                is UiEvent.Error -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.errorMessage,
                        duration = SnackbarDuration.Short
                    )
                }

                is UiEvent.IsLoading -> {}
            }
        }
    }

    resultRecipient.onNavResult { result ->
        when(result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                dineInViewModel.onEvent(DineInEvent.RefreshDineInOrder)
            }
        }
    }

    SentryTraced(tag = "DineInScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showTopBar = false,
            showBottomBar = true,
            bottomBar = {
                if (dineInOrders.isNotEmpty()) {
                    CartFooterPlaceOrder(
                        countTotalItems = countTotalDineInItems,
                        countSelectedItem = countSelectedDineInItem,
                        showPrintBtn = false,
                        onClickSelectAll = {
                            dineInViewModel.onEvent(DineInEvent.SelectAllDineInOrder)
                        },
                        onClickPlaceAllOrder = {
                            dineInViewModel.onEvent(DineInEvent.PlaceAllDineInOrder)
                        },
                        onClickPrintAllOrder = {}
                    )
                }
            }
        ) {
            SwipeRefresh(
                modifier = Modifier.fillMaxSize(),
                state = rememberSwipeRefreshState(isLoading),
                onRefresh = {
                    dineInViewModel.onEvent(DineInEvent.RefreshDineInOrder)
                }
            ) {
                if(isLoading){
                    LoadingIndicator()
                } else if(countTotalDineInItems == 0 || hasError != null) {
                    ItemNotAvailable(
                        text = hasError ?: stringResource(id = R.string.dine_in_orders_not_found),
                        buttonText = stringResource(id = R.string.add_items_to_cart_button),
                        image = painterResource(R.drawable.emptycart),
                        onClick = {
                            navController.navigate(MainFeedScreenDestination())
                        }
                    )
                } else {
                    CartItems(
                        listState = listState,
                        cartItems = dineInOrders,
                        selectedCartItems = selectedDineInOrder,
                        addOnItems = addOnItems,
                        onSelectCartOrder = {
                            dineInViewModel.onEvent(DineInEvent.SelectDineInOrder(it))
                        },
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickViewOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
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