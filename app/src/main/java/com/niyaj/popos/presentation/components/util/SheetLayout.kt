package com.niyaj.popos.presentation.components.util

import androidx.compose.runtime.Composable
import com.niyaj.popos.domain.util.BottomSheetScreen
import com.niyaj.popos.presentation.add_on_items.FilterAddOnItemScreen
import com.niyaj.popos.presentation.address.FilterAddressScreen
import com.niyaj.popos.presentation.cart_order.GetAndSelectCartOrderScreen
import com.niyaj.popos.presentation.category.FilterCategoryScreen
import com.niyaj.popos.presentation.charges.FilterChargesScreen
import com.niyaj.popos.presentation.customer.FilterCustomerScreen
import com.niyaj.popos.presentation.delivery_partner.FilterPartnerScreen
import com.niyaj.popos.presentation.employee.FilterEmployeeScreen
import com.niyaj.popos.presentation.expenses.FilterExpensesScreen
import com.niyaj.popos.presentation.expenses_category.FilterExpensesCategoryScreen
import com.niyaj.popos.presentation.order.FilterOrderScreen
import com.niyaj.popos.presentation.product.FilterProductScreen


@Composable
fun SheetLayout(
    currentScreen: BottomSheetScreen,
    onCloseBottomSheet: () -> Unit,
) {
    BottomSheetWithCloseDialog(
        text = currentScreen.type,
        onClosePressed = onCloseBottomSheet
    ){
        when(currentScreen){
            is BottomSheetScreen.GetAndSelectCartOrderScreen -> {
                GetAndSelectCartOrderScreen(
                    onClosePressed = onCloseBottomSheet
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