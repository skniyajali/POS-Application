package com.niyaj.popos.presentation.report

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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.niyaj.popos.presentation.components.ExtendedFabButton
import com.niyaj.popos.presentation.components.ItemNotAvailable
import com.niyaj.popos.presentation.components.RoundedBox
import com.niyaj.popos.presentation.components.StandardScaffold
import com.niyaj.popos.presentation.components.chart.common.dimens.ChartDimens
import com.niyaj.popos.presentation.components.chart.horizontalbar.HorizontalBarChart
import com.niyaj.popos.presentation.components.chart.horizontalbar.axis.HorizontalAxisConfig
import com.niyaj.popos.presentation.components.chart.horizontalbar.config.HorizontalBarConfig
import com.niyaj.popos.presentation.components.chart.horizontalbar.config.StartDirection
import com.niyaj.popos.presentation.destinations.ExpensesScreenDestination
import com.niyaj.popos.presentation.destinations.OrderScreenDestination
import com.niyaj.popos.presentation.destinations.ViewLastSevenDaysReportsDestination
import com.niyaj.popos.presentation.report.components.OrderTypeDropdown
import com.niyaj.popos.presentation.report.components.ReportBox
import com.niyaj.popos.presentation.ui.theme.ButtonSize
import com.niyaj.popos.presentation.ui.theme.KellyGreen
import com.niyaj.popos.presentation.ui.theme.LimeGreen
import com.niyaj.popos.presentation.ui.theme.MediumGray
import com.niyaj.popos.presentation.ui.theme.SpaceMedium
import com.niyaj.popos.presentation.ui.theme.SpaceMini
import com.niyaj.popos.presentation.ui.theme.SpaceSmall
import com.niyaj.popos.presentation.ui.theme.TextGray
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
import java.time.LocalDate

@Destination
@Composable
fun ReportScreen(
    navController: NavController,
    scaffoldState: ScaffoldState,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val lazyListState = rememberLazyListState()
    val dialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()

    val reportState = reportsViewModel.reportState.collectAsState().value
    val totalAmount = reportState.expensesAmount.plus(reportState.dineInSalesAmount).plus(reportState.dineOutSalesAmount).toString()

    val reportBarData = reportsViewModel.reportsBarData.collectAsState().value.reportBarData
    val reportBarIsLoading = reportsViewModel.reportsBarData.collectAsState().value.isLoading
    val reportBarError = reportsViewModel.reportsBarData.collectAsState().value.error

    val productWiseData = reportsViewModel.productWiseData.collectAsState().value.data
    val productDataIsLoading = reportsViewModel.productWiseData.collectAsState().value.isLoading
    val productDataError = reportsViewModel.productWiseData.collectAsState().value.error
    val orderType = reportsViewModel.productWiseData.collectAsState().value.orderType

    val selectedDate = reportsViewModel.selectedDate.collectAsState().value
    val lastSevenStartDate = getCalculatedStartDate("-8")


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
                onClick = { reportsViewModel.onReportEvent(ReportsEvent.PrintReport) }
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
                    date.toMilliSecond >= lastSevenStartDate && date <= LocalDate.now()
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
                if (reportBarIsLoading || productDataIsLoading) {
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

                        item("productWiseData") {
                            Spacer(modifier = Modifier.height(SpaceMedium))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                elevation = 2.dp,
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
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
                                                text = "Most Sales Products",
                                                style = MaterialTheme.typography.body1,
                                                fontWeight = FontWeight.Bold
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

                                        OrderTypeDropdown(
                                            text = orderType.ifEmpty { "All" }
                                        ) {
                                            reportsViewModel.onReportEvent(ReportsEvent.OnChangeOrderType(it))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(SpaceSmall))

                                    Divider(
                                        modifier = Modifier.fillMaxWidth(),
                                    )

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
                                            colors = listOf(LimeGreen, KellyGreen),
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
                            }

                            Spacer(modifier = Modifier.height(SpaceSmall))
                        }
                    }
                }
            }
        }
    }
}