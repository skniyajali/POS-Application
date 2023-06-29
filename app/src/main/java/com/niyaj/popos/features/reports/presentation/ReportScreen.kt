package com.niyaj.popos.features.reports.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.niyaj.popos.features.common.ui.theme.KellyGreen
import com.niyaj.popos.features.common.ui.theme.MediumGray
import com.niyaj.popos.features.common.ui.theme.PurpleHaze
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.CountBox
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.LoadingIndicator
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.StandardButton
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardFabButton
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.features.components.chart.horizontalbar.HorizontalBarChart
import com.niyaj.popos.features.components.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.StartDirection
import com.niyaj.popos.features.destinations.AddressDetailsScreenDestination
import com.niyaj.popos.features.destinations.CustomerDetailsScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ProductDetailsScreenDestination
import com.niyaj.popos.features.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.presentation.components.AddressReportCard
import com.niyaj.popos.features.reports.presentation.components.CategoryWiseReportCard
import com.niyaj.popos.features.reports.presentation.components.CustomerReportCard
import com.niyaj.popos.features.reports.presentation.components.OrderTypeDropdown
import com.niyaj.popos.features.reports.presentation.components.ReportBox
import com.niyaj.popos.utils.getCalculatedStartDate
import com.niyaj.popos.utils.toMilliSecond
import com.niyaj.popos.utils.toPrettyDate
import com.niyaj.popos.utils.toRupee
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
@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalComposeUiApi::class
)
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

    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

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

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

    SentryTraced(tag = "ReportScreen") {
        StandardScaffold(
            navController = navController,
            scaffoldState = scaffoldState,
            showBackArrow = true,
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
                    showScrollToTop = showScrollToTop.value,
                    visible = false,
                    onScrollToTopClick = {
                        scope.launch {
                            lazyListState.animateScrollToItem(index = 0)
                        }
                    },
                    onClick = {},
                )
            },
            floatingActionButtonPosition = if(showScrollToTop.value) FabPosition.End else FabPosition.Center,
        ) {

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
                modifier = Modifier.padding(SpaceSmall)
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
                            navController.navigate(AddressDetailsScreenDestination(it))
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
                            navController.navigate(CustomerDetailsScreenDestination(it))
                        }
                    )

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}


@Composable
fun ReportBoxData(
    report : Reports,
    onOrderClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onRefreshReport: () -> Unit
) {
    val totalAmount = report.expensesAmount.plus(report.dineInSalesAmount).plus(report.dineOutSalesAmount).toString()

    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
        crossAxisAlignment = FlowCrossAxisAlignment.Center,
        crossAxisSpacing = SpaceMini,
    ) {
        ReportBox(
            title = "DineIn Sales",
            amount = report.dineInSalesAmount.toString(),
            icon = Icons.Default.RamenDining,
            onClick = onOrderClick
        )

        ReportBox(
            title = "DineOut Sales",
            amount = report.dineOutSalesAmount.toString(),
            icon = Icons.Default.DeliveryDining,
            onClick = onOrderClick
        )

        ReportBox(
            title = "Expenses",
            amount = report.expensesAmount.toString(),
            icon = Icons.Default.Receipt,
            onClick = onExpensesClick
        )

        ReportBox(
            title = "Total Amount",
            amount = totalAmount,
            icon = Icons.Default.Money,
            enabled = false,
            onClick = {}
        )
    }

    Spacer(modifier = Modifier.height(SpaceSmall))

    StandardButton(
        modifier = Modifier.fillMaxWidth(),
        text = "Re-Generate Report",
        icon = Icons.Default.Sync,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colors.secondaryVariant
        ),
        onClick = onRefreshReport
    )
}

@Composable
fun ReportBarData(
    reportBarState: ReportsBarState,
    selectedBarData: String,
    onBarClick: (String) -> Unit,
    onClickViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 2.dp,
    ) {
        Crossfade(targetState = reportBarState, label = "ReportBarState") { state ->
            when {
                state.isLoading -> LoadingIndicator()

                state.reportBarData.isNotEmpty() -> {
                    val reportBarData = state.reportBarData

                    Column(
                        modifier = Modifier
                            .padding(SpaceSmall)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    text = "Last ${reportBarData.size} Days Reports",
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Bold
                                )
                                if (selectedBarData.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    Text(
                                        text = selectedBarData,
                                        style = MaterialTheme.typography.body2,
                                        color = MediumGray
                                    )
                                }
                            }

                            IconButton(
                                onClick = onClickViewDetails
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowRightAlt,
                                    contentDescription = "View Reports Details",
                                    tint = colors.secondary
                                )
                            }
                        }

                        Divider(modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(SpaceSmall))

                        HorizontalBarChart(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((reportBarData.size.times(60)).dp)
                                .padding(SpaceSmall),
                            onBarClick = {
                                onBarClick(
                                    "${it.yValue} - ${
                                        it.xValue.toString().substringBefore(".").toRupee
                                    }"
                                )
                            },
                            colors = listOf(colors.secondary, colors.secondaryVariant),
                            barDimens = ChartDimens(2.dp),
                            horizontalBarConfig = HorizontalBarConfig(
                                showLabels = false,
                                startDirection = StartDirection.Left,
                                productReport = false
                            ),
                            horizontalAxisConfig = HorizontalAxisConfig(
                                showAxes = true,
                                showUnitLabels = false
                            ),
                            horizontalBarData = reportBarData,
                        )
                    }
                }

                else -> {
                    ItemNotAvailable(
                        modifier = Modifier.padding(SpaceSmall),
                        text = state.error ?: "Reports are not available",
                        showImage = false,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryWiseReport(
    categoryState: CategoryWiseReportState,
    reportExpanded: Boolean,
    selectedCategory: String,
    onCategoryExpandChanged: (String) -> Unit,
    onExpandChanged: () -> Unit,
    onClickOrderType: (String) -> Unit,
    onProductClick: (productId: String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = reportExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Category Wise Report",
                    icon = Icons.Default.Category,
                    isTitle = true
                )
            },
            trailing = {
                OrderTypeDropdown(
                    text = categoryState.orderType.ifEmpty { "All" }
                ) {
                    onClickOrderType(it)
                }
            },
            expand = null,
            content = {
                Crossfade(targetState = categoryState, label = "CategoryState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.categoryWiseReport.isNotEmpty() -> {
                            CategoryWiseReportCard(
                                report = state.categoryWiseReport,
                                selectedCategory = selectedCategory,
                                onExpandChanged = onCategoryExpandChanged,
                                onProductClick = onProductClick
                            )
                        }
                        else -> {
                            ItemNotAvailable(
                                text = state.hasError ?: "Category wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
            contentDesc = "Category wise report"
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductWiseReport(
    productState: ProductWiseReportState,
    productRepExpanded: Boolean,
    selectedProduct: String,
    onExpandChanged : () -> Unit,
    onClickOrderType: (String) -> Unit,
    onBarClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            expanded = productRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                Column {
                    TextWithIcon(
                        text = "Product Wise Report",
                        icon = Icons.Default.Dns,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (selectedProduct.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(SpaceSmall))
                        Text(
                            text = selectedProduct,
                            style = MaterialTheme.typography.body2,
                            color = MediumGray
                        )
                    }
                }
            },
            trailing = {
                OrderTypeDropdown(
                    text = productState.orderType.ifEmpty { "All" },
                    onItemClick = onClickOrderType
                )
            },
            expand = null,
            content = {
                Crossfade(targetState = productState, label = "ProductState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()

                        state.data.isNotEmpty() -> {
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            HorizontalBarChart(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height((state.data.size.times(50)).dp)
                                    .padding(SpaceSmall),
                                onBarClick = {
                                    onBarClick(
                                        "${it.yValue} - ${
                                            it.xValue.toString().substringBefore(".")
                                        } Qty"
                                    )
                                },
                                colors = listOf(PurpleHaze, KellyGreen),
                                barDimens = ChartDimens(2.dp),
                                horizontalBarConfig = HorizontalBarConfig(
                                    showLabels = false,
                                    startDirection = StartDirection.Left
                                ),
                                horizontalAxisConfig = HorizontalAxisConfig(
                                    showAxes = true,
                                    showUnitLabels = false
                                ),
                                horizontalBarData = state.data,
                            )
                        }

                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Product wise report not available",
                                showImage = false,
                            )
                        }
                    }
                }
            },
            contentDesc = "Product wise report"
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressWiseReport(
    addressState: AddressWiseReportState,
    addressWiseRepExpanded: Boolean,
    onExpandChanged : () -> Unit,
    onAddressClick: (addressId: String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = addressWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Address Wise Report",
                    icon = Icons.Default.Business,
                    isTitle = true
                )
            },
            trailing = {
                CountBox(count = addressState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = addressState, label = "AddressState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    if (report.address != null) {
                                        AddressReportCard(
                                            report = report,
                                            onAddressClick = onAddressClick
                                        )

                                        if (index != state.reports.size - 1) {
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                            Divider(modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            ItemNotAvailable(
                                text = state.error ?: "Address wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Address wise report"
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomerWiseReport(
    customerState: CustomerWiseReportState,
    customerWiseRepExpanded: Boolean,
    onExpandChanged : () -> Unit,
    onCustomerClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
    ) {
        StandardExpandable(
            modifier = Modifier
                .padding(SpaceSmall)
                .fillMaxWidth(),
            expanded = customerWiseRepExpanded,
            onExpandChanged = {
                onExpandChanged()
            },
            title = {
                TextWithIcon(
                    text = "Customer Wise Report",
                    icon = Icons.Default.PeopleAlt,
                    isTitle = true
                )
            },
            trailing = {
                CountBox(count = customerState.reports.size.toString())
            },
            rowClickable = true,
            expand = null,
            content = {
                Crossfade(targetState = customerState, label = "CustomerState") { state ->
                    when {
                        state.isLoading -> LoadingIndicator()
                        state.reports.isNotEmpty() -> {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.reports.forEachIndexed { index, report ->
                                    if (report.customer != null) {
                                        CustomerReportCard(
                                            customerReport = report,
                                            onClickCustomer = onCustomerClick
                                        )

                                        if (index != state.reports.size - 1) {
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                            Divider(modifier = Modifier.fillMaxWidth())
                                            Spacer(modifier = Modifier.height(SpaceMini))
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            ItemNotAvailable(
                                text = "Customer wise report not available",
                                showImage = false
                            )
                        }
                    }
                }
            },
            contentDesc = "Customer wise report"
        )
    }
}