package com.niyaj.feature.order.details

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.feature.order.components.AddressDetails
import com.niyaj.feature.order.components.CartItemDetails
import com.niyaj.feature.order.components.CartOrderDetails
import com.niyaj.feature.order.components.CustomerDetails
import com.niyaj.feature.print.OrderPrintViewModel
import com.niyaj.feature.print.PrintEvent
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.components.StandardScaffold
import com.niyaj.ui.event.UiState
import com.ramcosta.composedestinations.annotation.Destination

/**
 * [OrderDetailsScreen] is the screen that displays the details of the order
 * @param cartOrderId is the id of the order
 * @param navController is the navController that handles the navigation
 * @param viewModel is the viewModel that handles the business logic of the screen
 * @param orderPrintViewModel is the viewModel that handles the business logic of the screen
 * @author Sk Niyaj Ali
 * @see OrderDetailsViewModel
 * @see OrderPrintViewModel
 */
@OptIn(ExperimentalPermissionsApi::class)
@Destination
@Composable
fun OrderDetailsScreen(
    cartOrderId: String,
    navController: NavController,
    onClickAddress: (String) -> Unit,
    onClickCustomer: (String) -> Unit,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    orderPrintViewModel: OrderPrintViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()
    val scaffoldState = rememberScaffoldState()

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
    ) {}

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

    val bluetoothManager = remember {
        context.getSystemService(BluetoothManager::class.java)
    }

    val bluetoothAdapter: BluetoothAdapter? = remember {
        bluetoothManager.adapter
    }

    val printOrder: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(cartOrderId))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintOrder(cartOrderId))
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val uiState = viewModel.orderDetails.collectAsStateWithLifecycle().value
    val charges = viewModel.charges.collectAsStateWithLifecycle().value

    var cartOrderExpended by remember {
        mutableStateOf(true)
    }

    var customerExpended by remember {
        mutableStateOf(false)
    }

    var addressExpended by remember {
        mutableStateOf(false)
    }

    var cartExpended by remember {
        mutableStateOf(true)
    }

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        navActions = {
            IconButton(
                onClick = printOrder,
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Print Order",
                    tint = MaterialTheme.colors.onSecondary,
                )
            }
        },
        title = {
            Text(text = "Order Details")
        }
    ) {
        Crossfade(
            targetState = uiState,
            label = "Order Detail::State"
        ) { state ->
            when(state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> ItemNotAvailable(text = "Order Details not available")

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = lazyListState,
                    ) {
                        item {
                            state.data.cartOrder?.let { cartOrder ->
                                CartOrderDetails(
                                    cartOrder = cartOrder,
                                    doesExpanded = cartOrderExpended,
                                    onExpandChanged = {
                                        cartOrderExpended = !cartOrderExpended
                                    }
                                )

                                cartOrder.customer?.let { customer ->
                                    CustomerDetails(
                                        customer = customer,
                                        doesExpanded = customerExpended,
                                        onExpandChanged = {
                                            customerExpended = !customerExpended
                                        },
                                        onClickViewDetails = onClickCustomer
                                    )
                                }

                                cartOrder.address?.let { address ->
                                    AddressDetails(
                                        address = address,
                                        doesExpanded = addressExpended,
                                        onExpandChanged = {
                                            addressExpended = !addressExpended
                                        },
                                        onClickViewDetails = onClickAddress
                                    )
                                }

                                if (state.data.orderedProducts.isNotEmpty()) {
                                    CartItemDetails(
                                        cartOrder = cartOrder,
                                        cartProduct = state.data.orderedProducts,
                                        charges = charges,
                                        orderPrice = state.data.orderPrice,
                                        doesExpanded = cartExpended,
                                        onExpandChanged = {
                                            cartExpended = !cartExpended
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}