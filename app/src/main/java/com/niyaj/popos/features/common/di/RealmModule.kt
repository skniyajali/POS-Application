package com.niyaj.popos.features.common.di

import com.niyaj.popos.features.account.domain.model.Account
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.printer_info.domain.model.PrinterRealm
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.profile.domain.model.RestaurantInfo
import com.niyaj.popos.features.reminder.domain.model.Reminder
import com.niyaj.popos.features.reports.domain.model.Reports
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    private val schema = setOf(
        Product::class,
        Category::class,
        Customer::class,
        Address::class,
        CartOrder::class,
        SelectedCartOrder::class,
        CartRealm::class,
        Charges::class,
        AddOnItem::class,
        ExpensesCategory::class,
        Expenses::class,
        Employee::class,
        EmployeeSalary::class,
        EmployeeAttendance::class,
        Reports::class,
        Settings::class,
        RestaurantInfo::class,
        Reminder::class,
        Account::class,
        PrinterRealm::class,
    )

    @Provides
    fun provideRealmConfig(): RealmConfiguration {
        return RealmConfiguration
            .Builder(schema)
            .name("popos.realm")
            .schemaVersion(1)
            .build()
    }
}