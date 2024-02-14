package com.niyaj.popos.navigation

import androidx.compose.ui.ExperimentalComposeUiApi
import com.niyaj.app_settings.SettingsNavGraph
import com.niyaj.cart_selected.CartselectedNavGraph
import com.niyaj.feature.account.AccountNavGraph
import com.niyaj.feature.addonitem.AddonitemNavGraph
import com.niyaj.feature.address.AddressNavGraph
import com.niyaj.feature.cart.CartNavGraph
import com.niyaj.feature.cart_order.CartorderNavGraph
import com.niyaj.feature.category.CategoryNavGraph
import com.niyaj.feature.charges.ChargesNavGraph
import com.niyaj.feature.customer.CustomerNavGraph
import com.niyaj.feature.employee.EmployeeNavGraph
import com.niyaj.feature.employee_attendance.EmployeeattendanceNavGraph
import com.niyaj.feature.employee_payment.EmployeepaymentNavGraph
import com.niyaj.feature.expenses.ExpensesNavGraph
import com.niyaj.feature.expenses_category.ExpensescategoryNavGraph
import com.niyaj.feature.home.HomeNavGraph
import com.niyaj.feature.order.OrderNavGraph
import com.niyaj.feature.printer.PrinterNavGraph
import com.niyaj.feature.product.ProductNavGraph
import com.niyaj.feature.profile.ProfileNavGraph
import com.niyaj.feature.reminder.ReminderNavGraph
import com.niyaj.feature.reports.ReportsNavGraph
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec

@ExperimentalComposeUiApi
object RootNavGraph : NavGraphSpec {

    override val route = "root"

    override val destinationsByRoute = emptyMap<String, DestinationSpec<*>>()

    override val startRoute = AccountNavGraph

    override val nestedNavGraphs = listOf(
        AccountNavGraph,
        AddonitemNavGraph,
        AddressNavGraph,
        SettingsNavGraph,
        CartNavGraph,
        CartorderNavGraph,
        CartselectedNavGraph,
        CategoryNavGraph,
        ChargesNavGraph,
        CustomerNavGraph,
        EmployeeNavGraph,
        EmployeeattendanceNavGraph,
        EmployeepaymentNavGraph,
        ExpensesNavGraph,
        ExpensescategoryNavGraph,
        HomeNavGraph,
        OrderNavGraph,
        PrinterNavGraph,
        ProductNavGraph,
        ProfileNavGraph,
        ReminderNavGraph,
        ReportsNavGraph,
    )
}