package com.niyaj.popos.features.reports.presentation

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.features.common.ui.theme.ButtonSize
import com.niyaj.popos.features.common.ui.theme.KellyGreen
import com.niyaj.popos.features.common.ui.theme.MediumGray
import com.niyaj.popos.features.common.ui.theme.PurpleHaze
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.common.ui.theme.TextGray
import com.niyaj.popos.features.components.CountBox
import com.niyaj.popos.features.components.ExtendedFabButton
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.RoundedBox
import com.niyaj.popos.features.components.StandardExpandable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.features.components.TextWithIcon
import com.niyaj.popos.features.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.features.components.chart.horizontalbar.HorizontalBarChart
import com.niyaj.popos.features.components.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.popos.features.components.chart.horizontalbar.config.StartDirection
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.popos.features.reports.presentation.components.CustomerReportCard
import com.niyaj.popos.features.reports.presentation.components.OrderTypeDropdown
import com.niyaj.popos.features.reports.presentation.components.ReportBox
import com.niyaj.popos.util.getCalculatedStartDate
import com.niyaj.popos.util.toFormattedDate
import com.niyaj.popos.util.toMilliSecond
import com.niyaj.popos.util.toRupee
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalPermissionsApi::class
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

    val reportState = reportsViewModel.reportState.collectAsStateWithLifecycle().value.report
    val totalAmount = reportState.expensesAmount.plus(reportState.dineInSalesAmount).plus(reportState.dineOutSalesAmount).toString()

    val reportBarData = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value.reportBarData
    val reportBarIsLoading = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value.isLoading
    val reportBarError = reportsViewModel.reportsBarData.collectAsStateWithLifecycle().value.error

    val productWiseData = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value.data
    val productDataIsLoading = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value.isLoading
    val productDataError = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value.error
    val orderType = reportsViewModel.productWiseData.collectAsStateWithLifecycle().value.orderType

    val selectedDate = reportsViewModel.selectedDate.collectAsStateWithLifecycle().value
    val lastSevenStartDate = getCalculatedStartDate("-8")

    val categoryWiseReport = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value.categoryWiseReport
    val groupedByCategoryWiseReport = categoryWiseReport.groupBy { it.product?.category?.categoryName }
    val categoryOrderType = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value.orderType
    val categoryDataIsLoading = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value.isLoading
    val categoryDataError = reportsViewModel.categoryWiseData.collectAsStateWithLifecycle().value.hasError

    val addressWiseReport = reportsViewModel.addressWiseData.collectAsStateWithLifecycle().value.reports
    val addressRepLoading = reportsViewModel.addressWiseData.collectAsStateWithLifecycle().value.isLoading
    val addressRepError = reportsViewModel.addressWiseData.collectAsStateWithLifecycle().value.error

    val customerWiseReport = reportsViewModel.customerWiseData.collectAsStateWithLifecycle().value.reports
    val customerRepLoading = reportsViewModel.customerWiseData.collectAsStateWithLifecycle().value.isLoading
    val customerRepError = reportsViewModel.customerWiseData.collectAsStateWithLifecycle().value.error

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

    val showScrollToTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 0
        }
    }

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
                    text = selectedDate.toFormattedDate,
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
            ExtendedFabButton(
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

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = reportBarIsLoading),
            onRefresh = {
                reportsViewModel.onReportEvent(ReportsEvent.RefreshReport)
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SpaceSmall),
            ) {
                if (reportBarIsLoading || productDataIsLoading || categoryDataIsLoading || customerRepLoading || addressRepLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else if (reportBarError != null) {
                    ItemNotAvailable(
                        text = reportBarError,
                    )
                }
                else if (productDataError != null) {
                    ItemNotAvailable(
                        text = productDataError,
                    )
                }
                else if (categoryDataError != null) {
                    ItemNotAvailable(
                        text = categoryDataError,
                    )
                }
                else if (customerRepError != null) {
                    ItemNotAvailable(
                        text = customerRepError,
                    )
                }
                else if (addressRepError != null) {
                    ItemNotAvailable(
                        text = addressRepError,
                    )
                }
                else {
                    LazyColumn(
                        state = lazyListState,
                    ) {

                        item("reportBoxData") {
                            Spacer(modifier = Modifier.height(SpaceSmall))
                            FlowRow(
                                mainAxisSize = SizeMode.Expand,
                                mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween,
                                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                                crossAxisSpacing = SpaceMini,
                            ) {
                                ReportBox(
                                    title = "DineIn Sales",
                                    amount = reportState.dineInSalesAmount.toString(),
                                    icon = Icons.Default.RamenDining,
                                    onClick = {
                                        navController.navigate(OrderScreenDestination())
                                    }
                                )

                                ReportBox(
                                    title = "DineOut Sales",
                                    amount = reportState.dineOutSalesAmount.toString(),
                                    icon = Icons.Default.DeliveryDining,
                                    onClick = {
                                        navController.navigate(OrderScreenDestination())
                                    }
                                )

                                ReportBox(
                                    title = "Expenses",
                                    amount = reportState.expensesAmount.toString(),
                                    icon = Icons.Default.Receipt,
                                    onClick = {
                                        navController.navigate(ExpensesScreenDestination)
                                    }
                                )

                                ReportBox(
                                    title = "Total Amount",
                                    amount = totalAmount,
                                    icon = Icons.Default.Money,
                                    enabled = false,
                                    onClick = {

                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(SpaceSmall))

                            Button(
                                onClick = {
                                    reportsViewModel.onReportEvent(ReportsEvent.RefreshReport)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(ButtonSize),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = colors.secondaryVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Re-Generate Report",
                                )
                                Spacer(modifier = Modifier.width(SpaceMini))
                                Text(
                                    text = "Re-Generate Report".uppercase(),
                                    style = MaterialTheme.typography.button,
                                )
                            }
                        }

                        item("reportBarData") {
                            Spacer(modifier = Modifier.height(SpaceMedium))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                elevation = 2.dp,
                            ) {
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
                                            onClick = {
                                                navController.navigate(ViewLastSevenDaysReportsDestination)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowRightAlt,
                                                contentDescription = "View Reports Details",
                                                tint = colors.secondary
                                            )
                                        }
                                    }

                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                    )

                                    Spacer(modifier = Modifier.height(SpaceSmall))
                                    if(reportBarData.isNotEmpty()){
                                        HorizontalBarChart(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((reportBarData.size.times(60)).dp)
                                                .padding(SpaceSmall),
                                            onBarClick = {
                                                selectedBarData = "${it.yValue} - ${
                                                    it.xValue.toString().substringBefore(".").toRupee
                                                }"
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


                                    } else {
                                        Text(
                                            text = "Report not available.",
                                            color = TextGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                }
                            }
                        }

                        item("categoryWiseReport") {
                            Spacer(modifier = Modifier.height(SpaceMedium))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                StandardExpandable(
                                    modifier = Modifier
                                        .padding(SpaceSmall)
                                        .fillMaxWidth(),
                                    expanded = categoryWiseRepExpanded,
                                    onExpandChanged = {
                                        categoryWiseRepExpanded = !categoryWiseRepExpanded
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
                                            text = categoryOrderType.ifEmpty { "All" }
                                        ) {
                                            reportsViewModel.onReportEvent(ReportsEvent.OnChangeCategoryOrderType(it))
                                        }
                                    },
                                    expand = null,
                                    content = {
                                        if (groupedByCategoryWiseReport.isEmpty()) {
                                            Text(
                                                text = "Category wise report not available",
                                                color = TextGray,
                                                textAlign = TextAlign.Center
                                            )
                                        } else {
                                            groupedByCategoryWiseReport.forEach { (category, products) ->
                                                if (category != null && products.isNotEmpty()){
                                                    val totalQuantity = products.sumOf { it.quantity }.toString()

                                                    StandardExpandable(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(SpaceSmall),
                                                        expanded = category == selectedCategory,
                                                        onExpandChanged = {
                                                            reportsViewModel.onReportEvent(ReportsEvent.OnSelectCategory(category))
                                                        },
                                                        title = {
                                                            TextWithIcon(
                                                                text = category,
                                                                icon = Icons.Default.Category,
                                                                isTitle = true
                                                            )
                                                        },
                                                        trailing = {
                                                            CountBox(count = totalQuantity)
                                                        },
                                                        rowClickable = true,
                                                        expand = { modifier: Modifier ->
                                                            IconButton(
                                                                modifier = modifier,
                                                                onClick = {
                                                                    reportsViewModel.onReportEvent(ReportsEvent.OnSelectCategory(category))
                                                                }
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                                                    contentDescription = "Expand More",
                                                                    tint = colors.secondary
                                                                )
                                                            }
                                                        },
                                                        content = {
                                                            val sortedProducts = products.sortedByDescending { it.quantity}

                                                            sortedProducts.forEachIndexed { index, productWithQty ->
                                                                if (productWithQty.product != null) {
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(SpaceSmall),
                                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                    ) {
                                                                        Text(
                                                                            text = productWithQty.product.productName,
                                                                            style = MaterialTheme.typography.body1,
                                                                            textAlign = TextAlign.Start,
                                                                            fontWeight = FontWeight.SemiBold,
                                                                        )

                                                                        Text(
                                                                            text = productWithQty.quantity.toString(),
                                                                            style = MaterialTheme.typography.h6,
                                                                            textAlign = TextAlign.End,
                                                                            fontWeight = FontWeight.SemiBold,
                                                                            color = colors.secondaryVariant,
                                                                            modifier = Modifier.weight(0.5F)
                                                                        )
                                                                    }
                                                                    if (index != products.size - 1) {
                                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                                        Divider(modifier = Modifier.fillMaxWidth())
                                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    )

                                                    Spacer(modifier = Modifier.height(SpaceMini))
                                                    Divider(modifier = Modifier.fillMaxWidth())
                                                    Spacer(modifier = Modifier.height(SpaceMini))
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }

                        item("productWiseData") {
                            Spacer(modifier = Modifier.height(SpaceMedium))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                StandardExpandable(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(SpaceSmall),
                                    expanded = productWiseRepExpanded,
                                    onExpandChanged = {
                                        productWiseRepExpanded = !productWiseRepExpanded
                                    },
                                    title = {
                                        Column {
                                            TextWithIcon(
                                                text = "Product Wise Report",
                                                icon = Icons.Default.Dns,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            if (selectedProductData.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(SpaceSmall))
                                                Text(
                                                    text = selectedProductData,
                                                    style = MaterialTheme.typography.body2,
                                                    color = MediumGray
                                                )
                                            }
                                        }
                                    },
                                    trailing = {
                                        OrderTypeDropdown(
                                            text = orderType.ifEmpty { "All" }
                                        ) {
                                            reportsViewModel.onReportEvent(ReportsEvent.OnChangeOrderType(it))
                                        }
                                    },
                                    expand = null,
                                    content = {
                                        if (productWiseData.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(SpaceSmall))

                                            HorizontalBarChart(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((productWiseData.size.times(50)).dp)
                                                    .padding(SpaceSmall),
                                                onBarClick = {
                                                    selectedProductData = "${it.yValue} - ${
                                                        it.xValue.toString().substringBefore(".")
                                                    } Qty"
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
                                                horizontalBarData = productWiseData,
                                            )
                                        } else {
                                            Text(
                                                text = "Product wise report not available",
                                                color = TextGray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item("addressWiseReport") {
                            Spacer(modifier = Modifier.height(SpaceMedium))
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
                                        addressWiseRepExpanded = !addressWiseRepExpanded
                                    },
                                    title = {
                                        TextWithIcon(
                                            text = "Address Wise Report",
                                            icon = Icons.Default.Business,
                                            isTitle = true
                                        )
                                    },
                                    trailing = {
                                        CountBox(count = addressWiseReport.size.toString())
                                    },
                                    rowClickable = true,
                                    expand = null,
                                    content = {
                                        if (addressWiseReport.isNotEmpty()){
                                            addressWiseReport.forEachIndexed { index, address ->
                                                if (address.address != null) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(SpaceSmall),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically,
                                                    ) {
                                                        Text(
                                                            text = address.address.addressName,
                                                            style = MaterialTheme.typography.body1,
                                                            textAlign = TextAlign.Start,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier.weight(2F)
                                                        )

                                                        Text(
                                                            text = address.address.shortName,
                                                            style = MaterialTheme.typography.body2,
                                                            textAlign = TextAlign.End,
                                                            fontWeight = FontWeight.SemiBold,
                                                            modifier = Modifier.weight(0.5F)
                                                        )

                                                        Text(
                                                            text = address.orderQty.toString(),
                                                            style = MaterialTheme.typography.h6,
                                                            textAlign = TextAlign.End,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = colors.secondaryVariant,
                                                            modifier = Modifier.weight(0.5F)
                                                        )
                                                    }

                                                    if (index != addressWiseReport.size - 1) {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Divider(modifier = Modifier.fillMaxWidth())
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                    }
                                                }
                                            }
                                        }else {
                                            Text(
                                                text = "Address wise report not available",
                                                color = TextGray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                )
                            }
                        }

                        item("customerWiseReport") {
                            Spacer(modifier = Modifier.height(SpaceMedium))
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
                                        customerWiseRepExpanded = !customerWiseRepExpanded
                                    },
                                    title = {
                                        TextWithIcon(
                                            text = "Customer Wise Report",
                                            icon = Icons.Default.PeopleAlt,
                                            isTitle = true
                                        )
                                    },
                                    trailing = {
                                        CountBox(count = customerWiseReport.size.toString())
                                    },
                                    rowClickable = true,
                                    expand = null,
                                    content = {
                                        if (customerWiseReport.isNotEmpty()){
                                            customerWiseReport.forEachIndexed { index, customer ->
                                                if (customer.customer != null) {
                                                    CustomerReportCard(
                                                        customerPhoneNo = customer.customer.customerPhone,
                                                        customerName = customer.customer.customerName,
                                                        customerEmail = customer.customer.customerEmail,
                                                        orderQty = customer.orderQty
                                                    )

                                                    if (index != customerWiseReport.size - 1) {
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                        Divider(modifier = Modifier.fillMaxWidth())
                                                        Spacer(modifier = Modifier.height(SpaceMini))
                                                    }
                                                }
                                            }
                                        }else {
                                            Text(
                                                text = "Customer wise report not available",
                                                color = TextGray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }

                    }
                }
            }
        }
    }
}
