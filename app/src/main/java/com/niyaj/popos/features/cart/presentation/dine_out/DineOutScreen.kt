package com.niyaj.popos.features.cart.presentation.dine_out

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
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
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.niyaj.popos.features.order.presentation.print_order.PrintViewModel
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * DineOutScreen is the screen where the user can see all the dine out orders
 * @author Sk Niyaj Ali
 * @param navController
 * @param scaffoldState
 * @param dineOutViewModel
 * @param addOnItemViewModel
 * @param printViewModel
 * @param resultRecipient
 * @see DineOutViewModel
 * @see AddOnItemViewModel
 * @see PrintViewModel
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DineOutScreen(
    navController: NavController,
    scaffoldState : ScaffoldState,
    dineOutViewModel: DineOutViewModel = hiltViewModel(),
    addOnItemViewModel: AddOnItemViewModel = hiltViewModel(),
    printViewModel: PrintViewModel = hiltViewModel(),
    resultRecipient : ResultRecipient<AddEditCartOrderScreenDestination, String>
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
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed){
                        navController.navigate(OrderScreenDestination())
                    }
                }

                is UiEvent.OnError -> {
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
                dineOutViewModel.onEvent(DineOutEvent.RefreshDineOutOrder)
            }
        }
    }
    
    SentryTraced(tag = "DineOutScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showTopBar = false,
            bottomBar = {
                if (dineOutOrders.isNotEmpty()) {
                    CartFooterPlaceOrder(
                        countTotalItems = countTotalDineOutItems,
                        countSelectedItem = countSelectedDineOutItem,
                        showPrintBtn = true,
                        onClickSelectAll = {
                            dineOutViewModel.onEvent(DineOutEvent.SelectAllDineOutOrder)
                        },
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
            SwipeRefresh(
                state = rememberSwipeRefreshState(isLoading),
                onRefresh = {
                    dineOutViewModel.onEvent(DineOutEvent.RefreshDineOutOrder)
                }
            ) {
                if(isLoading){
                    LoadingIndicator()
                } else if(dineOutOrders.isEmpty() || hasError != null) {
                    ItemNotAvailable(
                        text = hasError ?: stringResource(id = R.string.dine_out_orders_not_found),
                        buttonText = stringResource(id = R.string.add_items_to_cart_button),
                        image = painterResource(R.drawable.emptycarttwo),
                        onClick = {
                            navController.navigate(MainFeedScreenDestination())
                        }
                    )
                } else{
                    CartItems(
                        listState = listState,
                        cartItems = dineOutOrders,
                        selectedCartItems = selectedDineOutOrder,
                        addOnItems = addOnItems,
                        showPrintBtn = true,
                        onSelectCartOrder = {
                            dineOutViewModel.onEvent(DineOutEvent.SelectDineOutOrder(it))
                        },
                        onClickEditOrder = {
                            navController.navigate(AddEditCartOrderScreenDestination(it))
                        },
                        onClickViewOrder = {
                            navController.navigate(OrderDetailsScreenDestination(it))
                        },
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