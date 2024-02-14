package com.niyaj.feature.order

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER
import com.niyaj.common.tags.OrderTestTags.DELETE_ORDER_MESSAGE
import com.niyaj.common.utils.toBarDate
import com.niyaj.common.utils.toMilliSecond
import com.niyaj.core.ui.R
import com.niyaj.designsystem.theme.SpaceMini
import com.niyaj.feature.order.components.DineInOrderedItemLayout
import com.niyaj.feature.order.components.DineOutOrderedItemLayout
import com.niyaj.feature.order.destinations.OrderDetailsScreenDestination
import com.niyaj.feature.print.OrderPrintViewModel
import com.niyaj.feature.print.PrintEvent
import com.niyaj.ui.components.RoundedBox
import com.niyaj.ui.components.StandardScaffoldNew
import com.niyaj.ui.components.StandardSearchBar
import com.niyaj.ui.event.UiEvent
import com.niyaj.ui.util.CartTabItem
import com.niyaj.ui.util.Screens
import com.niyaj.ui.util.Tabs
import com.niyaj.ui.util.TabsContent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

/**
 * Order Screen
 * @author Sk Niyaj Ali
 * @param selectedDate
 * @param navController
 * @param scaffoldState
 * @param viewModel
 * @param orderPrintViewModel
 * @see OrderViewModel
 * @see OrderPrintViewModel
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@RootNavGraph(start = true)
@Destination(route = Screens.ORDER_SCREEN)
@Composable
fun OrderScreen(
    selectedDate: String = "",
    navController: NavController,
    scaffoldState: ScaffoldState,
    onClickEditOrder: (String) -> Unit,
    viewModel: OrderViewModel = hiltViewModel(),
    orderPrintViewModel: OrderPrintViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState { 2 }
    val dialogState = rememberMaterialDialogState()
    val deleteOrderState = rememberMaterialDialogState()

    val selectedDate1 = viewModel.selectedDate.value

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
    ) {}

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
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate1))
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                orderPrintViewModel.onPrintEvent(PrintEvent.PrintDeliveryReport(selectedDate1))
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

    val showSearchBar = viewModel.showSearchBar.collectAsStateWithLifecycle().value
    val searchText = viewModel.searchText.value

    val dineInOrders = viewModel.dineInOrders.collectAsStateWithLifecycle().value
    val dineInIsEmpty = viewModel.dineInIsEmpty

    val dineOutOrders = viewModel.dineOutOrders.collectAsStateWithLifecycle().value
    val dineOutIsEmpty = viewModel.dineOutIsEmpty

    var deletableOrder by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.Success -> {
                    val result = scaffoldState.snackbarHostState.showSnackbar(
                        message = event.successMessage,
                        actionLabel = "View",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        navController.navigate(Screens.CART_SCREEN)
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

    BackHandler(true) {
        if (showSearchBar) {
            viewModel.closeSearchBar()
        } else {
            navController.navigateUp()
        }
    }

    StandardScaffoldNew(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackButton = true,
        showBottomBar = false, // dineInOrders.isNotEmpty() || dineOutOrders.isNotEmpty(),
        onBackClick = {
            if (showSearchBar) {
                viewModel.closeSearchBar()
            } else {
                navController.navigateUp()
            }
        },
        title = "Orders",
        navActions = {
            if (showSearchBar) {
                StandardSearchBar(
                    searchText = searchText,
                    placeholderText = "Search for orders...",
                    onSearchTextChanged = viewModel::searchTextChanged,
                    onClearClick = viewModel::clearSearchText,
                )
            } else {
                val showIcon = if (pagerState.currentPage == 1) !dineInIsEmpty else !dineOutIsEmpty

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
                        onClick = viewModel::openSearchBar
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
        },
        floatingActionButton = {},
        selectionCount = 0,
    ) { paddingValues ->
        val tabs = listOf(
            CartTabItem.DineOutItem {
                DineOutOrderedItemLayout(
                    dineOutState = dineOutOrders,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = printOrder,
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    },
                    onClickViewDetails = {
                        navController.navigate(OrderDetailsScreenDestination(it))
                    },
                    onClickEdit = onClickEditOrder,
                    onClickAddProduct = {
                        navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
                    },
                )
            },
            CartTabItem.DineInItem {
                DineInOrderedItemLayout(
                    dineInState = dineInOrders,
                    showSearchBar = showSearchBar,
                    onClickPrintOrder = printOrder,
                    onClickDelete = {
                        deleteOrderState.show()
                        deletableOrder = it
                    },
                    onMarkedAsProcessing = {
                        viewModel.onOrderEvent(OrderEvent.MarkedAsProcessing(it))
                    },
                    onClickViewDetails = {
                        navController.navigate(OrderDetailsScreenDestination(it))
                    },
                    onClickEdit = onClickEditOrder,
                    onClickAddProduct = {
                        navController.navigate(Screens.ADD_EDIT_PRODUCT_SCREEN)
                    },
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Tabs(tabs = tabs, pagerState = pagerState)
            TabsContent(tabs = tabs, pagerState = pagerState)
        }
    }

    MaterialDialog(
        dialogState = deleteOrderState,
        buttons = {
            positiveButton(
                text = "Delete",
                onClick = {
                    viewModel.onOrderEvent(OrderEvent.DeleteOrder(deletableOrder))
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
        title(text = DELETE_ORDER)
        message(text = DELETE_ORDER_MESSAGE)
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
            viewModel.onOrderEvent(OrderEvent.SelectDate(date.toMilliSecond))
        }
    }

}