package com.niyaj.popos.features.components.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.popos.features.addon_item.presentation.FilterAddOnItemScreen
import com.niyaj.popos.features.address.presentation.FilterAddressScreen
import com.niyaj.popos.features.cart_order.presentation.GetAndSelectCartOrderScreen
import com.niyaj.popos.features.category.presentation.FilterCategoryScreen
import com.niyaj.popos.features.charges.presentation.FilterChargesScreen
import com.niyaj.popos.features.common.util.BottomSheetScreen
import com.niyaj.popos.features.customer.presentation.FilterCustomerScreen
import com.niyaj.popos.features.delivery_partner.presentation.FilterPartnerScreen
import com.niyaj.popos.features.employee.presentation.FilterEmployeeScreen
import com.niyaj.popos.features.expenses.presentation.FilterExpensesScreen
import com.niyaj.popos.features.expenses_category.presentation.FilterExpensesCategoryScreen
import com.niyaj.popos.features.order.presentation.FilterOrderScreen
import com.niyaj.popos.features.product.presentation.FilterProductScreen


@Composable
fun SheetLayout(
    currentScreen: BottomSheetScreen,
    onCloseBottomSheet: () -> Unit,
    navController: NavController = rememberNavController(),
) {
    BottomSheetWithCloseDialog(
        text = currentScreen.type,
        onClosePressed = onCloseBottomSheet
    ){
        when(currentScreen){
            is BottomSheetScreen.GetAndSelectCartOrderScreen -> {
                GetAndSelectCartOrderScreen(
                    onClosePressed = onCloseBottomSheet,
                    navController = navController,
                )
            }

            is BottomSheetScreen.FilterCategoryScreen -> {
                FilterCategoryScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterCategory = currentScreen.filterCategory,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterProductScreen -> {
                FilterProductScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterProduct = currentScreen.filterProduct,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterAddressScreen -> {
                FilterAddressScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterAddress = currentScreen.filterAddress,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterCustomerScreen -> {
                FilterCustomerScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterCustomer = currentScreen.filterCustomer,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterOrderScreen -> {
                FilterOrderScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterOrder = currentScreen.filterOrder,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterAddOnItemScreen -> {
                FilterAddOnItemScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterAddOnItem = currentScreen.filterAddOnItem,
                    onFilterChanged = currentScreen.onFilterChanged,
                )
            }

            is BottomSheetScreen.FilterChargesScreen -> {
                FilterChargesScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterCharges = currentScreen.filterCharges,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }
            is BottomSheetScreen.FilterPartnerScreen -> {
                FilterPartnerScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterPartner = currentScreen.filterPartner,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterEmployeeScreen -> {
                FilterEmployeeScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterEmployee = currentScreen.filterEmployee,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterExpensesCategoryScreen -> {
                FilterExpensesCategoryScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterExpensesCategory = currentScreen.filterExpensesCategory,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            is BottomSheetScreen.FilterExpensesScreen -> {
                FilterExpensesScreen(
                    onClosePressed = onCloseBottomSheet,
                    filterExpenses = currentScreen.filterExpenses,
                    onFilterChanged = currentScreen.onFilterChanged
                )
            }

            else -> {}

        }
    }
}