package com.niyaj.popos.features.cart.presentation.dine_out

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.features.addon_item.presentation.AddOnItemViewModel
import com.niyaj.popos.features.cart.presentation.components.CartAddOnItems
import com.niyaj.popos.features.cart.presentation.components.CartFooterPlaceOrder
import com.niyaj.popos.features.cart.presentation.components.CartItemOrderDetailsSection
import com.niyaj.popos.features.cart.presentation.components.CartItemProductDetailsSection
import com.niyaj.popos.features.cart.presentation.components.CartItemTotalPriceSection
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.MainFeedScreenDestination
import com.niyaj.popos.features.destinations.OrderDetailsScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.niyaj.popos.features.order.presentation.print_order.PrintViewModel
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalPermissionsApi::class
)
@Composable
fun DineOutScreen(
    navController: NavController,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    dineOutViewModel: DineOutViewModel = hiltViewModel(),
    addOnItemViewModel: AddOnItemViewModel = hiltViewModel(),
    printViewModel: PrintViewModel = hiltViewModel(),
) {

    val context = LocalContext.current

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Timber.d("bluetoothLauncher", "Success")
        } else {
            Timber.w("bluetoothLauncher", "Failed")
        }
    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: (String) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val printAllOrder: (List<String>) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                printViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                printViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val listState = rememberLazyListState()

    val dineOutOrders = dineOutViewModel.dineOutOrders.collectAsStateWithLifecycle().value.cartItems
    val isLoading = dineOutViewModel.dineOutOrders.collectAsStateWithLifecycle().value.isLoading
    val hasError = dineOutViewModel.dineOutOrders.collectAsStateWithLifecycle().value.error

    val countTotalDineOutItems = dineOutViewModel.dineOutOrders.collectAsStateWithLifecycle().value.cartItems.size
    val selectedDineOutOrder = dineOutViewModel.selectedDineOutOrder.collectAsStateWithLifecycle().value
    val countSelectedDineOutItem = selectedDineOutOrder.size

    val addOnItems = addOnItemViewModel.state.collectAsStateWithLifecycle().value.addOnItems

    LaunchedEffect(key1 = true){
        dineOutViewModel.eventFlow.collectLatest { event ->
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
            dineOutViewModel.onDineOutEvent(DineOutEvent.RefreshDineOutOrder)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if(isLoading){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    CircularProgressIndicator()
                }
            } else if(countTotalDineOutItems == 0 || hasError != null) {
                ItemNotAvailable(
                    text = hasError ?: stringResource(id = R.string.dine_out_orders_not_found),
                    buttonText = stringResource(id = R.string.add_items_to_cart_button),
                    onClick = {
                        navController.navigate(MainFeedScreenDestination())
                    }
                )
            } else{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(2.7f)
                        .padding(SpaceSmall),
                    verticalArrangement = Arrangement.Top,
                    state = listState
                ) {
                    items(
                        items = dineOutOrders,
                    ){ cartItem ->
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
                                        dineOutViewModel.onDineOutEvent(
                                            DineOutEvent.SelectDineOutOrder(
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
                                        selected = selectedDineOutOrder.contains(cartItem.cartOrder.cartOrderId),
                                        onClick = {
                                            dineOutViewModel.onDineOutEvent(
                                                DineOutEvent.SelectDineOutOrder(
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                        },
                                        onEditClick = {
                                            navController.navigate(AddEditCartOrderScreenDestination(cartOrderId = cartItem.cartOrder.cartOrderId))
                                        },
                                        onViewClick = {
                                            navController.navigate(OrderDetailsScreenDestination(cartOrderId = cartItem.cartOrder.cartOrderId))
                                        }
                                    )

                                    Spacer(modifier = Modifier.height(SpaceMini))

                                    CartItemProductDetailsSection(
                                        cartProducts = cartItem.cartProducts,
                                        decreaseQuantity = {
                                            dineOutViewModel.onDineOutEvent(
                                                DineOutEvent.RemoveProductFromCart(
                                                    cartItem.cartOrder.cartOrderId,
                                                    it
                                                )
                                            )
                                        },
                                        increaseQuantity = {
                                            dineOutViewModel.onDineOutEvent(
                                                DineOutEvent.AddProductToCart(
                                                    cartItem.cartOrder.cartOrderId,
                                                    it
                                                )
                                            )
                                        }
                                    )


                                    if(addOnItems.isNotEmpty()){
                                        val cartAddOnItem = cartItem.cartOrder.addOnItems

                                        Spacer(modifier = Modifier.height(SpaceSmall))

                                        val selectedAddOnItems = cartAddOnItem.map {
                                            it.addOnItemId
                                        }

                                        CartAddOnItems(
                                            addOnItems = addOnItems,
                                            selectedAddOnItem = selectedAddOnItems,
                                            onClick = {
                                                dineOutViewModel.onDineOutEvent(
                                                    DineOutEvent.UpdateAddOnItemInCart(
                                                        it,
                                                        cartItem.cartOrder.cartOrderId
                                                    )
                                                )
                                            },
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    CartItemTotalPriceSection(
                                        itemCount = cartItem.cartProducts.size,
                                        orderType = cartItem.cartOrder.orderType,
                                        totalPrice = cartItem.orderPrice.first,
                                        discountPrice = cartItem.orderPrice.second,
                                        onClickPlaceOrder = {
                                            dineOutViewModel.onDineOutEvent(
                                                DineOutEvent.PlaceDineOutOrder(
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                        },
                                        onClickPrintOrder = {
                                            dineOutViewModel.onDineOutEvent(
                                                DineOutEvent.PlaceDineOutOrder(
                                                    cartItem.cartOrder.cartOrderId
                                                )
                                            )
                                            printOrder(cartItem.cartOrder.cartOrderId)
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }

                CartFooterPlaceOrder(
                    modifier = Modifier.weight(0.3f, true),
                    countTotalItems = countTotalDineOutItems,
                    countSelectedItem = countSelectedDineOutItem,
                    onClickSelectAll = {
                        dineOutViewModel.onDineOutEvent(DineOutEvent.SelectAllDineOutOrder)
                    },
                    onClickPlaceOrder = {
                        dineOutViewModel.onDineOutEvent(DineOutEvent.PlaceAllDineOutOrder)
                    },
                    onClickPrintOrder = {
                        dineOutViewModel.onDineOutEvent(DineOutEvent.PlaceAllDineOutOrder)
                        printAllOrder(selectedDineOutOrder)
                    }
                )
            }
        }
    }
}