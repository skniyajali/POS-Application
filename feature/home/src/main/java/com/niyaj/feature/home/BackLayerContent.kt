package com.niyaj.feature.home

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
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.ui.components.IconBox
import com.niyaj.ui.components.ReportBox
import com.niyaj.ui.util.Screens
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
    val reportState = viewModel.reportState.collectAsStateWithLifecycle().value
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
                onClick = { navController.navigate(Screens.CART_SCREEN) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Inventory,
                onClick = { navController.navigate(Screens.ORDER_SCREEN) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Assessment,
                onClick = { navController.navigate(Screens.REPORT_SCREEN) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Money,
                onClick = { navController.navigate(Screens.EXPENSES_SCREEN) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.People,
                onClick = { navController.navigate(Screens.EMPLOYEE_SCREEN) }
            )
            Spacer(modifier = Modifier.width(SpaceSmall))
            IconBox(
                iconName = Icons.Default.Notifications,
                onClick = { navController.navigate(Screens.REMINDER_SCREEN) }
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
                    navController.navigate(Screens.ORDER_SCREEN)
                }
            )

            ReportBox(
                title = "DineOut Sales",
                amount = reportState.dineOutSalesAmount.toString(),
                icon = Icons.Default.DeliveryDining,
                onClick = {
                    navController.navigate(Screens.ORDER_SCREEN)
                }
            )

            ReportBox(
                title = "Expenses",
                amount = reportState.expensesAmount.toString(),
                icon = Icons.Default.Receipt,
                onClick = {
                    navController.navigate(Screens.EXPENSES_SCREEN)
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