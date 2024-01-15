package com.niyaj.popos.features.order.presentation

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.R
import com.niyaj.popos.common.utils.toBarDate
import com.niyaj.popos.common.utils.toMilliSecond
import com.niyaj.popos.features.cart.presentation.CartTabItem
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.util.UiEvent
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.StandardSearchBar
import com.niyaj.popos.features.components.util.Tabs
import com.niyaj.popos.features.components.util.TabsContent
import com.niyaj.popos.features.destinations.AddEditCartOrderScreenDestination
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.order.presentation.dine_in.DineInOrderScreen
import com.niyaj.popos.features.order.presentation.dine_out.DineOutOrderScreen
import com.niyaj.popos.features.order.presentation.print_order.OrderPrintViewModel
import com.niyaj.popos.features.order.presentation.print_order.PrintEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.time.LocalDate

/**
 * Order Screen
 * @author Sk Niyaj Ali
 * @param selectedDate
 * @param navController
 * @param scaffoldState
 * @param orderViewModel
 * @param orderPrintViewModel
 * @param resultRecipient
 * @see OrderViewModel
 * @see OrderPrintViewModel
 */
@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Destination
@Composable
fun OrderScreen(
    selectedDate: String = "",
    navController: NavController,
    scaffoldState: ScaffoldState,
    orderViewModel: OrderViewModel = hiltViewModel(),
    orderPrintViewModel: OrderPrintViewModel = hiltViewModel(),
    resultRecipient: ResultRecipient<AddEditCartOrderScreenDestination, String>
) {
    val context = LocalContext.current

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
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

    val printDeliveryReport: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                orderViewModel.onOrderEvent(OrderEvent.PrintDeliveryReport)
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderViewModel.onOrderEvent(OrderEvent.PrintDeliveryReport)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
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

    val pagerState = rememberPagerState { 2 }
    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    val showSearchBar by orderViewModel.toggledSearchBar.collectAsStateWithLifecycle()

    val dineInOrders = orderViewModel.dineInOrders.collectAsStateWithLifecycle().value.dineInOrders
    val dineInLoading: Boolean =
        orderViewModel.dineInOrders.collectAsStateWithLifecycle().value.isLoading
    val dineInError = orderViewModel.dineInOrders.collectAsStateWithLifecycle().value.error

    val dineOutOrders =
        orderViewModel.dineOutOrders.collectAsStateWithLifecycle().value.dineOutOrders
    val dineOutLoading: Boolean =
        orderViewModel.dineOutOrders.collectAsStateWithLifecycle().value.isLoading
    val dineOutError = orderViewModel.dineOutOrders.collectAsStateWithLifecycle().value.error

    val selectedDate1 = orderViewModel.selectedDate.value

    var deletableOrder by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        orderViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        navController.navigate(CartScreenDestination())
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

    BackHandler(true) {
        if (showSearchBar) {
            orderViewModel.onSearchBarCloseAndClearClick()
        } else {
            navController.navigateUp()
        }
    }

    resultRecipient.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> {}
            is NavResult.Value -> {
                orderViewModel.onOrderEvent(OrderEvent.RefreshOrder)
            }
        }
    }

    SentryTraced(tag = "OrderScreen-$selectedDate") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            showBottomBar = false, // dineInOrders.isNotEmpty() || dineOutOrders.isNotEmpty(),
            onBackButtonClick = {
                if (showSearchBar) {
                    orderViewModel.onSearchBarCloseAndClearClick()
                } else {
                    navController.navigateUp()
                }
            },
            navigationIcon = {},
            title = {
                Text(text = "Orders")
            },
            navActions = {
                if (showSearchBar) {
                    StandardSearchBar(
                        searchText = orderViewModel.searchText.collectAsState().value,
                        placeholderText = "Search for orders...",
                        onSearchTextChanged = {
                            orderViewModel.onOrderEvent(OrderEvent.OnSearchOrder(it))
                        },
                        onClearClick = {
                            orderViewModel.onSearchTextClearClick()
                        },
                    )
                } else {
                    val showIcon =
                        if (pagerState.currentPage == 1) dineInOrders.isNotEmpty() else dineOutOrders.isNotEmpty()

                    if (selectedDate1.isNotEmpty()) {
                        RoundedBox(
                            text = selectedDate1.toBarDate,
                            onClick = {
                                dialogState.show()
                            }
                        )
                        Spacer(modifier = Modifier.width(SpaceMini))
                    } else {
                        IconButton(
                            onClick = { dialogState.show() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Choose Date"
                            )
                        }
                    }

                    if (showIcon) {
                        IconButton(
                            onClick = {
                                orderViewModel.onOrderEvent(OrderEvent.ToggleSearchBar)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(id = R.string.search_icon),
                                tint = MaterialTheme.colors.onPrimary,
                            )
                        }

                        if (pagerState.currentPage == 0) {
                            IconButton(
                                onClick = {
                                    printDeliveryReport()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = "Print Delivery Reports",
                                    tint = MaterialTheme.colors.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        ) {
            MaterialDialog(
                dialogState = deleteOrderState,
                buttons = {
                    positiveButton(
                        text = "Delete",
                        onClick = {
                            orderViewModel.onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
                            deletableOrder = ""
                        }
                    )
                    negativeButton(
                        text = "Cancel",
                        onClick = {
                            deleteOrderState.hide()
                            deletableOrder = ""
                        },
                    )
                }
            ) {
                title(text = "Delete Order?")
                message(res = R.string.delete_order_msg)
            }

            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker(
                    allowedDateValidator = { date ->
                        date <= LocalDate.now()
                    }
                ) { date ->
                    orderViewModel.onOrderEvent(OrderEvent.SelectDate(date.toMilliSecond))
                }
            }

            val tabs = listOf(
                CartTabItem.DineOutItem {
                    DineOutOrderScreen(
                        navController = navController,
                        dineOutOrders = dineOutOrders,
                        isLoading = dineOutLoading,
                        error = dineOutError,
                        showSearchBar = showSearchBar,
                        onClickPrintOrder = {
                            printOrder(it)
                        },
                        onClickDeleteOrder = {
                            deleteOrderState.show()
                            deletableOrder = it
                        },
                        onMarkedAsDelivered = {
                            orderViewModel.onOrderEvent(OrderEvent.MarkedAsDelivered(it))
                        },
                        onMarkedAsProcessing = {
                            orderViewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                        }
                    )
                },
                CartTabItem.DineInItem {
                    DineInOrderScreen(
                        navController = navController,
                        dineInOrders = dineInOrders,
                        isLoading = dineInLoading,
                        error = dineInError,
                        showSearchBar = showSearchBar,
                        onClickPrintOrder = {
                            printOrder(it)
                        },
                        onClickDeleteOrder = {
                            deleteOrderState.show()
                            deletableOrder = it
                        },
                        onMarkedAsDelivered = {
                            orderViewModel.onOrderEvent(OrderEvent.MarkedAsDelivered(it))
                        },
                        onMarkedAsProcessing = {
                            orderViewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                        }
                    )
                }
            )

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = dineInLoading || dineOutLoading),
                onRefresh = {
                    orderViewModel.onOrderEvent(OrderEvent.RefreshOrder)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Tabs(tabs = tabs, pagerState = pagerState)
                    TabsContent(tabs = tabs, pagerState = pagerState)
                }
            }
        }
    }
}