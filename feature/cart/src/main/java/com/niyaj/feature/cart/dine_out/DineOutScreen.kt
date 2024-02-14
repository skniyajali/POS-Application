package com.niyaj.feature.cart.dine_out

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.core.ui.R
import com.niyaj.feature.cart.components.CartFooterPlaceOrder
import com.niyaj.feature.cart.components.CartItems
import com.niyaj.feature.print.OrderPrintViewModel
import com.niyaj.feature.print.PrintEvent
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.Screens
import kotlinx.coroutines.flow.collectLatest

/**
 * DineOutScreen is the screen where the user can see all the dine out orders
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param dineOutViewModel
 * @param orderPrintViewModel
 * @see DineOutViewModel
 * @see OrderPrintViewModel
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DineOutScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    dineOutViewModel: DineOutViewModel = hiltViewModel(),
    orderPrintViewModel: OrderPrintViewModel = hiltViewModel(),
    onClickEditOrder: (String) -> Unit,
    onClickViewOrder: (String) -> Unit,
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
    ) { }

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
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val printAllOrder: (List<String>) -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrders(it))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val listState = rememberLazyListState()

    val uiState = dineOutViewModel.dineOutOrders.collectAsStateWithLifecycle().value

    val selectedDineOutOrder = dineOutViewModel.selectedItems.toList()
    val countSelectedDineOutItem = selectedDineOutOrder.size

    val addOnItems = dineOutViewModel.addOnItems.collectAsStateWithLifecycle().value

    LaunchedEffect(key1 = true) {
        dineOutViewModel.eventFlow.collectLatest { event ->
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
        label = "DineOut Cart::State"
    ) { state ->
        when (state) {
            is UiState.Loading -> LoadingIndicator()

            is UiState.Empty -> {
                ItemNotAvailable(
                    text = stringResource(id = R.string.dine_out_orders_not_found),
                    buttonText = stringResource(id = R.string.add_items_to_cart_button),
                    image = R.drawable.emptycarttwo,
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
                                countSelectedItem = countSelectedDineOutItem,
                                showPrintBtn = true,
                                onClickSelectAll = dineOutViewModel::selectAllItems,
                                onClickPlaceAllOrder = {
                                    dineOutViewModel.onEvent(DineOutEvent.PlaceAllDineOutOrder)
                                },
                                onClickPrintAllOrder = {
                                    dineOutViewModel.onEvent(DineOutEvent.PlaceAllDineOutOrder)
                                    printAllOrder(selectedDineOutOrder)
                                }
                            )
                        }
                    }
                ) {

                    CartItems(
                        listState = listState,
                        cartItems = state.data,
                        selectedCartItems = selectedDineOutOrder,
                        addOnItems = addOnItems,
                        showPrintBtn = true,
                        onSelectCartOrder = dineOutViewModel::selectItem,
                        onClickEditOrder = onClickEditOrder,
                        onClickViewOrder = onClickViewOrder,
                        onClickDecreaseQty = { cartOrderId, productId ->
                            dineOutViewModel.onEvent(
                                DineOutEvent.DecreaseQuantity(cartOrderId, productId)
                            )
                        },
                        onClickIncreaseQty = { cartOrderId, productId ->
                            dineOutViewModel.onEvent(
                                DineOutEvent.IncreaseQuantity(cartOrderId, productId)
                            )
                        },
                        onClickAddOnItem = { addOnItemId, cartOrderId ->
                            dineOutViewModel.onEvent(
                                DineOutEvent.UpdateAddOnItemInCart(addOnItemId, cartOrderId)
                            )
                        },
                        onClickPlaceOrder = {
                            dineOutViewModel.onEvent(DineOutEvent.PlaceDineOutOrder(it))
                        },
                        onClickPrintOrder = {
                            printOrder(it)
                            dineOutViewModel.onEvent(DineOutEvent.PlaceDineOutOrder(it))
                        },
                    )
                }
            }
        }
    }
}