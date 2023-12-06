package com.niyaj.popos.features.main_feed.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.niyaj.popos.features.common.ui.theme.SpaceMedium
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.destinations.CartScreenDestination
import com.niyaj.popos.features.destinations.EmployeeScreenDestination
import com.niyaj.popos.features.destinations.ExpensesScreenDestination
import com.niyaj.popos.features.destinations.OrderScreenDestination
import com.niyaj.popos.features.destinations.ReminderScreenDestination
import com.niyaj.popos.features.destinations.ReportScreenDestination
import com.niyaj.popos.features.main_feed.presentation.components.IconBox
import com.niyaj.popos.features.reports.presentation.components.ReportBox
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

/**
 * Main Feed Back Layer Component
 * @author Sk Niyaj Ali
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BackLayerContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BackLayerViewModel = hiltViewModel(),
) {
    val reportState = viewModel.reportState.collectAsStateWithLifecycle().value.report
    val totalAmount = reportState.expensesAmount.plus(reportState.dineInSalesAmount)
        .plus(reportState.dineOutSalesAmount).toString()

    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            viewModel.generateReport()
        }
    }

    Column(
        modifier = modifier
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
                onClick = { navController.navigate(CartScreenDestination()) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Inventory,
                onClick = { navController.navigate(OrderScreenDestination()) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Assessment,
                onClick = { navController.navigate(ReportScreenDestination()) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Money,
                onClick = { navController.navigate(ExpensesScreenDestination()) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.People,
                onClick = { navController.navigate(EmployeeScreenDestination()) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Notifications,
                onClick = { navController.navigate(ReminderScreenDestination) }
            )
        }

        Spacer(modifier = Modifier.height(SpaceMedium))
        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Center
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
                onClick = {}
            )
        }
    }
}