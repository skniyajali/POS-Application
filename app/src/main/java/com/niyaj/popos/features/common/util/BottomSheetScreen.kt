package com.niyaj.popos.features.common.util

import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.FILTER_ADD_ON_ITEM
import com.niyaj.popos.features.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.features.address.domain.util.FilterAddress
import com.niyaj.popos.features.category.domain.util.FilterCategory
import com.niyaj.popos.features.charges.domain.util.FilterCharges
import com.niyaj.popos.features.customer.domain.util.FilterCustomer
import com.niyaj.popos.features.employee.domain.util.FilterEmployee
import com.niyaj.popos.features.expenses.domain.util.FilterExpenses
import com.niyaj.popos.features.expenses_category.domain.util.FilterExpensesCategory
import com.niyaj.popos.features.order.domain.util.FilterOrder
import com.niyaj.popos.features.product.domain.util.FilterProduct

sealed class BottomSheetScreen(val type: String, val route: String){

    object GetAndSelectCartOrderScreen: BottomSheetScreen(type = "Choose Order", route = "select_cart_order_screen")

    data class FilterCategoryScreen(
        val filterCategory: FilterCategory = FilterCategory.ByCategoryId(SortType.Ascending),
        val onFilterChanged: (FilterCategory) -> Unit
    ): BottomSheetScreen(type = "Filter Category", route = "filter_category_screen")

    data class FilterProductScreen(
        val filterProduct: FilterProduct = FilterProduct.ByProductId(SortType.Ascending),
        val onFilterChanged: (FilterProduct) -> Unit
    ): BottomSheetScreen(type = "Filter Product", route = "filter_product_screen")

    data class FilterAddressScreen(
        val filterAddress: FilterAddress,
        val onFilterChanged: (FilterAddress) -> Unit
    ): BottomSheetScreen(type = "Filter Address", route = "filter_address_screen")

    data class FilterCustomerScreen(
        val filterCustomer: FilterCustomer,
        val onFilterChanged: (FilterCustomer) -> Unit
    ): BottomSheetScreen(type = "Filter Customer", route = "filter_customer_screen")

    data class FilterOrderScreen(
        val filterOrder: FilterOrder,
        val onFilterChanged: (FilterOrder) -> Unit
    ): BottomSheetScreen(type = "Filter Order", route = "filter_order_screen")

    data class FilterAddOnItemScreen(
        val filterAddOnItem: FilterAddOnItem,
        val onFilterChanged: (FilterAddOnItem) -> Unit
    ): BottomSheetScreen(type = FILTER_ADD_ON_ITEM, route = "filter_add_on_item_screen")

    data class FilterChargesScreen(
        val filterCharges: FilterCharges,
        val onFilterChanged: (FilterCharges) -> Unit
    ): BottomSheetScreen(type = "Filter Charges Item", route = "filter_charges_screen")

    data class FilterEmployeeScreen(
        val filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
        val onFilterChanged: (FilterEmployee) -> Unit
    ): BottomSheetScreen(type = "Filter Employee", route = "filter_employee_screen")

    data class FilterExpensesCategoryScreen(
        val filterExpensesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(
            SortType.Descending
        ),
        val onFilterChanged: (FilterExpensesCategory) -> Unit
    ): BottomSheetScreen(type = "Filter Expenses Category", route = "filter_expenses_category_screen")

    data class FilterExpensesScreen(
        val filterExpenses: FilterExpenses = FilterExpenses.ByExpensesId(SortType.Descending),
        val onFilterChanged: (FilterExpenses) -> Unit
    ): BottomSheetScreen(type = "Filter Expenses", route = "filter_expenses_screen")
}
