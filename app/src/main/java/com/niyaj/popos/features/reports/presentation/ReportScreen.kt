package com.niyaj.popos.features.reports.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.common.utils.getCalculatedStartDate
import com.niyaj.popos.common.utils.isScrolled
import com.niyaj.popos.common.utils.toMilliSecond
import com.niyaj.popos.common.utils.toPrettyDate
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ProductDetailsScreenDestination
import com.niyaj.popos.features.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.popos.features.reports.presentation.components.AddressWiseReport
import com.niyaj.popos.features.reports.presentation.components.CategoryWiseReport
import com.niyaj.popos.features.reports.presentation.components.CustomerWiseReport
import com.niyaj.popos.features.reports.presentation.components.ProductWiseReport
import com.niyaj.popos.features.reports.presentation.components.ReportBarData
import com.niyaj.popos.features.reports.presentation.components.ReportBoxData
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import io.sentry.compose.SentryTraced
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

/**
 * Report Screen
 * @author Sk Niyaj Ali
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class)
@Destination
@Composable
fun ReportScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
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

    val printReport: () -> Unit = {
        if (bluetoothPermissions.allPermissionsGranted) {
            if (bluetoothAdapter?.isEnabled == true) {
                // Bluetooth is on print the receipt
                reportsViewModel.onReportEvent(ReportsEvent.PrintReport)
            } else {
                // Bluetooth is off, ask user to turn it on
                enableBluetoothContract.launch(enableBluetoothIntent)
                reportsViewModel.onReportEvent(ReportsEvent.PrintReport)
            }
        } else {
            bluetoothPermissions.launchMultiplePermissionRequest()
        }
    }

    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()

    val report = reportsViewModel.reportState.collectAsStateWithLifecycle().value.report

    val reportBarState = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value

    val productState = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value

    val selectedDate = reportsViewModel.selectedDate.collectAsStateWithLifecycle().value
    val lastSevenStartDate = getCalculatedStartDate("-8")

    val categoryState = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value

    val addressState = reportsViewModel.addressWiseData.collectAsStateWithLifecycle().value

    val customerState = reportsViewModel.customerWiseData.collectAsStateWithLifecycle().value

    val selectedCategory = reportsViewModel.selectedCategory.collectAsStateWithLifecycle().value

    var categoryWiseRepExpanded by remember { mutableStateOf(false) }

    var productWiseRepExpanded by remember { mutableStateOf(false) }

    var customerWiseRepExpanded by remember { mutableStateOf(false) }

    var addressWiseRepExpanded by remember { mutableStateOf(false) }

    var selectedBarData by remember {
        mutableStateOf("")
    }

    var selectedProductData by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = selectedDate) {
        selectedBarData = ""
        selectedProductData = ""
    }

    SentryTraced(tag = "ReportScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
            showBottomBar = false,
            title = {
                Text(text = "Reports")
            },
            navActions = {
                if (selectedDate.isNotEmpty() && selectedDate != LocalDate.now().toString()) {
                    RoundedBox(
                        text = selectedDate.toPrettyDate(),
                        onClick = {
                            dialogState.show()
                        }
                    )
                    Spacer(modifier = Modifier.width(SpaceMini))
                } else {
                    IconButton(
                        onClick = { dialogState.show() }
                    ) {
                        Icon(imageVector = Icons.Default.Today, contentDescription = "Choose Date")
                    }
                }

                IconButton(
                    onClick = printReport,
                ) {
                    Icon(imageVector = Icons.Default.Print, contentDescription = "Print Reports")
                }

            },
            isFloatingActionButtonDocked = true,
            floatingActionButton = {
                StandardFabButton(
                    text = "",
                    showScrollToTop = lazyListState.isScrolled,
                    visible = false,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {},
                )
            },
            floatingActionButtonPosition = FabPosition.End,
        ) { paddingValues ->
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok")
                    negativeButton("Cancel")
                }
            ) {
                datepicker(
                    allowedDateValidator = { date ->
                        (date.toMilliSecond >= lastSevenStartDate) && (date <= LocalDate.now())
                    }
                ) { date ->
                    reportsViewModel.onReportEvent(ReportsEvent.SelectDate(date.toString()))
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(SpaceSmall)
            ) {
                item("reportBoxData") {
                    Spacer(modifier = Modifier.height(SpaceMini))

                    ReportBoxData(
                        report = report,
                        onOrderClick = {
                            navController.navigate(OrderScreenDestination())
                        },
                        onExpensesClick = {
                            navController.navigate(ExpensesScreenDestination)
                        },
                        onRefreshReport = {
                            reportsViewModel.onReportEvent(ReportsEvent.RefreshReport)
                        }
                    )
                }

                item("reportBarData") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    ReportBarData(
                        reportBarState = reportBarState,
                        selectedBarData = selectedBarData,
                        onBarClick = {
                            selectedBarData = it
                        },
                        onClickViewDetails = {
                            navController.navigate(ViewLastSevenDaysReportsDestination)
                        }
                    )
                }

                item("categoryWiseReport") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    CategoryWiseReport(
                        categoryState = categoryState,
                        reportExpanded = categoryWiseRepExpanded,
                        selectedCategory = selectedCategory,
                        onCategoryExpandChanged = {
                            reportsViewModel.onReportEvent(ReportsEvent.OnSelectCategory(it))
                        },
                        onExpandChanged = {
                            categoryWiseRepExpanded = !categoryWiseRepExpanded
                        },
                        onClickOrderType = {
                            reportsViewModel.onReportEvent(ReportsEvent.OnChangeCategoryOrderType(it))
                        },
                        onProductClick = {
                            navController.navigate(ProductDetailsScreenDestination(it))
                        }
                    )
                }

                item("productWiseReport") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    ProductWiseReport(
                        productState = productState,
                        productRepExpanded = productWiseRepExpanded,
                        selectedProduct = selectedProductData,
                        onExpandChanged = {
                            productWiseRepExpanded = !productWiseRepExpanded
                        },
                        onClickOrderType = {
                            reportsViewModel.onReportEvent(ReportsEvent.OnChangeOrderType(it))
                        },
                        onBarClick = {
                            selectedProductData = it
                        }
                    )
                }

                item("addressWiseReport") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    AddressWiseReport(
                        addressState = addressState,
                        addressWiseRepExpanded = addressWiseRepExpanded,
                        onExpandChanged = {
                            addressWiseRepExpanded = !addressWiseRepExpanded
                        },
                        onAddressClick = {
                            navController.navigate(
                                com.niyaj.popos.features.destinations.AddressDetailsScreenDestination(
                                    it
                                )
                            )
                        }
                    )
                }

                item("customerWiseReport") {
                    Spacer(modifier = Modifier.height(SpaceMedium))

                    CustomerWiseReport(
                        customerState = customerState,
                        customerWiseRepExpanded = customerWiseRepExpanded,
                        onExpandChanged = {
                            customerWiseRepExpanded = !customerWiseRepExpanded
                        },
                        onCustomerClick = {
                            navController.navigate(
                                com.niyaj.popos.features.destinations.CustomerDetailsScreenDestination(
                                    it
                                )
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}