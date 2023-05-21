package com.niyaj.popos.features.order.presentation.dine_in

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.niyaj.popos.features.order.domain.model.DineInOrder
import com.niyaj.popos.features.order.presentation.components.DineInOrderedItemLayout

@Composable
fun DineInOrderScreen(
    navController: NavController,
    dineInOrders: List<DineInOrder> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (String) -> Unit,
    onClickDeleteOrder: (String) -> Unit,
    onMarkedAsDelivered: (String) -> Unit,
    onMarkedAsProcessing: (String) -> Unit
) {
    DineInOrderedItemLayout(
        navController = navController,
        dineInOrders = dineInOrders,
        isLoading = isLoading,
        error = error,
        showSearchBar = showSearchBar,
        onClickPrintOrder = {
            onClickPrintOrder(it)
        },
        onClickDelete = {
            onClickDeleteOrder(it)
        },
        onMarkedAsDelivered = {
            onMarkedAsDelivered(it)
        },
        onMarkedAsProcessing = {
            onMarkedAsProcessing(it)
        }
    )
}