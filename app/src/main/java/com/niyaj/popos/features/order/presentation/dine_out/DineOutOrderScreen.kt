package com.niyaj.popos.features.order.presentation.dine_out

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.niyaj.popos.features.order.domain.model.DineOutOrder
import com.niyaj.popos.features.order.presentation.components.DineOutOrderedItemLayout

/**
 * DineOutOrderScreen is the main screen for dine out orders.
 * @author Sk Niyaj Ali
 * @param navController is the navigation controller for the screen.
 * @param dineOutOrders is the list of dine out orders.
 * @param isLoading is the loading state of the screen.
 * @param error is the error state of the screen.
 * @param showSearchBar is the search bar state of the screen.
 * @param onClickPrintOrder is the click listener for print order button.
 * @param onClickDeleteOrder is the click listener for delete order button.
 * @param onMarkedAsDelivered is the click listener for marked as delivered button.
 * @param onMarkedAsProcessing is the click listener for marked as processing button.
 * @see DineOutOrderedItemLayout
 */
@Composable
fun DineOutOrderScreen(
    navController: NavController,
    dineOutOrders: List<DineOutOrder> = emptyList(),
    isLoading: Boolean = false,
    error: String? = null,
    showSearchBar: Boolean = false,
    onClickPrintOrder: (String) -> Unit,
    onClickDeleteOrder: (String) -> Unit,
    onMarkedAsDelivered: (String) -> Unit,
    onMarkedAsProcessing: (String) -> Unit
) {
    DineOutOrderedItemLayout(
        navController = navController,
        dineOutOrders = dineOutOrders,
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