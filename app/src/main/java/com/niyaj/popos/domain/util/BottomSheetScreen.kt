package com.niyaj.popos.domain.util

import com.niyaj.popos.domain.util.filter_items.*
import com.niyaj.popos.realm.addon_item.presentation.AddOnItemViewModel
import com.niyaj.popos.realm.address.presentation.AddressViewModel
import com.niyaj.popos.realm.category.presentation.CategoryViewModel
import com.niyaj.popos.realm.charges.presentation.ChargesViewModel
import com.niyaj.popos.realm.customer.presentation.CustomerViewModel
import com.niyaj.popos.realm.expenses.presentation.ExpensesViewModel
import com.niyaj.popos.realm.expenses_category.presentation.ExpensesCategoryViewModel
import com.niyaj.popos.presentation.product.ProductsViewModel
import com.niyaj.popos.realm.addon_item.domain.util.FilterAddOnItem
import com.niyaj.popos.realm.address.domain.util.FilterAddress
import com.niyaj.popos.realm.category.domain.util.FilterCategory
import com.niyaj.popos.realm.charges.domain.util.FilterCharges
import com.niyaj.popos.realm.customer.domain.util.FilterCustomer
import com.niyaj.popos.realm.delivery_partner.domain.util.FilterPartner
import com.niyaj.popos.realm.employee.domain.util.FilterEmployee
import com.niyaj.popos.realm.expenses.domain.util.FilterExpenses
import com.niyaj.popos.realm.expenses_category.domain.util.FilterExpensesCategory

sealed class BottomSheetScreen(val type: String, val route: String){

    object CreateCartOrderScreen: BottomSheetScreen(type = "Create New Order", route = "create_cart_order_screen")

    object UpdateCartOrderScreen: BottomSheetScreen(type = "Update Order", route = "update_cart_order_screen")

    object GetAndSelectCartOrderScreen: BottomSheetScreen(type = "Choose Order", route = "select_cart_order_screen")

    data class EditCartOrderScreen(val cartOrderId: String? = null): BottomSheetScreen(type = "Update Order", route = "edit_cart_order_screen")

    object CartOrderSettingsScreen: BottomSheetScreen(type = "Cart Order Settings", route = "cart_order_setting_screen")

    data class CreateCategoryScreen( val categoryViewModel: CategoryViewModel): BottomSheetScreen(type = "Create New Category", route = "create_category_screen")

    data class EditCategoryScreen(val categoryId: String? = null, val categoryViewModel: CategoryViewModel): BottomSheetScreen(type = "Update Category", route = "update_category_screen")

    data class CreateProductScreen(val productsViewModel: ProductsViewModel): BottomSheetScreen(type = "Create New Product", route = "create_product_screen")

    data class EditProductScreen(val productId: String? = null, val productsViewModel: ProductsViewModel): BottomSheetScreen(type = "Update Product", route = "update_product_screen")

    data class CreateAddressScreen(val addressViewModel: AddressViewModel): BottomSheetScreen(type = "Create New Address", route = "create_address_screen")

    data class EditAddressScreen(val addressId: String? = null, val addressViewModel: AddressViewModel): BottomSheetScreen(type = "Update Address", route = "update_address_screen")

    data class CreateCustomerScreen(val customerViewModel: CustomerViewModel): BottomSheetScreen(type = "Create New Customer", route = "create_customer_screen")

    data class EditCustomerScreen(val customerId: String? = null, val customerViewModel: CustomerViewModel): BottomSheetScreen(type = "Update Customer", route = "update_customer_screen")

    data class CreateAddOnItemScreen(val addOnItemViewModel: AddOnItemViewModel): BottomSheetScreen(type = "Create New AddOn Item", route = "create_add_on_item_screen")

    data class EditAddOnItemScreen(val addOnItemId: String? = null, val addOnItemViewModel: AddOnItemViewModel): BottomSheetScreen(type = "Update AddOn Item", route = "update_add_on_item_screen")

    data class CreateChargesScreen(val chargesViewModel: ChargesViewModel): BottomSheetScreen(type = "Create New Charges Item", route = "create_charges_screen")

    data class EditChargesScreen(val chargesId: String? = null, val chargesViewModel: ChargesViewModel): BottomSheetScreen(type = "Update Charges Item", route = "update_charges_screen")

    data class CreateExpensesCategoryScreen(val expensesCategoryViewModel: ExpensesCategoryViewModel): BottomSheetScreen(type = "Create New Expenses Category", route = "create_expenses_category_screen")

    data class EditExpensesCategoryScreen(val expensesCategoryId: String? = null, val expensesCategoryViewModel: ExpensesCategoryViewModel): BottomSheetScreen(type = "Update Expenses Category", route = "update_expenses_category_screen")

    data class CreateExpensesScreen(val expensesViewModel: ExpensesViewModel): BottomSheetScreen(type = "Create New Expenses", route = "create_expenses_screen")

    data class EditExpensesScreen(val expensesId: String? = null, val expensesViewModel: ExpensesViewModel): BottomSheetScreen(type = "Update Expenses", route = "update_expenses_screen")


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
    ): BottomSheetScreen(type = "Filter AddOn Item", route = "filter_add_on_item_screen")

    data class FilterChargesScreen(
        val filterCharges: FilterCharges,
        val onFilterChanged: (FilterCharges) -> Unit
    ): BottomSheetScreen(type = "Filter Charges Item", route = "filter_charges_screen")

    data class FilterEmployeeScreen(
        val filterEmployee: FilterEmployee = FilterEmployee.ByEmployeeId(SortType.Descending),
        val onFilterChanged: (FilterEmployee) -> Unit
    ): BottomSheetScreen(type = "Filter Employee", route = "filter_employee_screen")

    data class FilterPartnerScreen(
        val filterPartner: FilterPartner = FilterPartner.ByPartnerId(SortType.Descending),
        val onFilterChanged: (FilterPartner) -> Unit
    ): BottomSheetScreen(type = "Filter Delivery Partner", route = "filter_delivery_partner_screen")

    data class FilterExpensesCategoryScreen(
        val filterExpensesCategory: FilterExpensesCategory = FilterExpensesCategory.ByExpensesCategoryId(SortType.Descending),
        val onFilterChanged: (FilterExpensesCategory) -> Unit
    ): BottomSheetScreen(type = "Filter Expenses Category", route = "filter_expenses_category_screen")

    data class FilterExpensesScreen(
        val filterExpenses: FilterExpenses = FilterExpenses.ByExpensesId(SortType.Descending),
        val onFilterChanged: (FilterExpenses) -> Unit
    ): BottomSheetScreen(type = "Filter Expenses", route = "filter_expenses_screen")
}
