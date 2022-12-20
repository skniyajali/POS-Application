package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.IconBox
import com.niyaj.popos.features.reports.presentation.ReportsViewModel
import com.niyaj.popos.features.reports.presentation.components.ReportBox
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BackLayerContent(
    navController: NavController,
    backdropScaffoldState: BackdropScaffoldState,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    val reportState = reportsViewModel.reportState.collectAsState().value
    val totalAmount = reportState.expensesAmount.plus(reportState.dineInSalesAmount).plus(reportState.dineOutSalesAmount).toString()

    Column(
        modifier = Modifier
            .padding(SpaceSmall)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(SpaceMedium))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconBox(
                iconName = Icons.Default.ShoppingCart,
                onClick = {navController.navigate(CartScreenDestination())}
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Inventory,
                onClick = {navController.navigate(OrderScreenDestination())}
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Assessment,
                onClick = {navController.navigate(ReportScreenDestination())}
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Money,
                onClick = {navController.navigate(ExpensesScreenDestination())}
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.People,
                onClick = {navController.navigate(EmployeeScreenDestination())}
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            AnimatedVisibility(
                visible = !backdropScaffoldState.isConcealed,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                IconBox(
                    iconName = Icons.Default.ArrowUpward,
                    onClick = {
                        scope.launch {
                            backdropScaffoldState.conceal()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(SpaceMedium))
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
    }
}