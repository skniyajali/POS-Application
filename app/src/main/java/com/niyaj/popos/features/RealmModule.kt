package com.niyaj.popos.features

import com.niyaj.popos.features.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.model.Settings
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.data.repository.CartRepositoryImpl
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.data.repository.CartOrderRepositoryImpl
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.features.category.domain.model.Category
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.charges.data.repository.ChargesRepositoryImpl
import com.niyaj.popos.features.charges.domain.model.Charges
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.data_deletion.data.repository.DataDeletionRepositoryImpl
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.features.delivery_partner.data.repository.PartnerRepositoryImpl
import com.niyaj.popos.features.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.features.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.features.employee.data.repository.EmployeeRepositoryImpl
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee_attendance.data.repository.AttendanceRepositoryImpl
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_salary.data.repository.SalaryRepositoryImpl
import com.niyaj.popos.features.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.expenses.data.repository.ExpensesRepositoryImpl
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses_category.data.repository.ExpensesCategoryRepositoryImpl
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.features.main_feed.data.repository.MainFeedRepositoryImpl
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.order.data.repository.OrderRepositoryImpl
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.features.product.data.repository.ProductRepositoryImpl
import com.niyaj.popos.features.product.domain.model.Product
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.reports.data.repository.ReportsRepositoryImpl
import com.niyaj.popos.features.reports.domain.model.Reports
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    private val schema = setOf(
        Category::class,
        Product::class,
        Customer::class,
        Address::class,
        CartOrder::class,
        SelectedCartOrder::class,
        CartRealm::class,
        Charges::class,
        AddOnItem::class,
        DeliveryPartner::class,
        ExpensesCategory::class,
        Expenses::class,
        Employee::class,
        EmployeeSalary::class,
        EmployeeAttendance::class,
        Reports::class,
        Settings::class
    )

    private val config = RealmConfiguration
        .Builder(schema)
        .deleteRealmIfMigrationNeeded()
        .name("popos.realm")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideMainFeedServiceImpl(): MainFeedRepository {
        return MainFeedRepositoryImpl(config)
    }

    @Provides
    fun provideCategoryRealmDaoImpl(): CategoryRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    fun provideProductRealmDaoImpl(): ProductRepository {
        return ProductRepositoryImpl(config)
    }

    @Provides
    fun provideCustomerRealmDaoImpl(): CustomerRepository {
        return CustomerRepositoryImpl(config)
    }

    @Provides
    fun provideAddressRealmDaoImpl(): AddressRepository {
        return AddressRepositoryImpl(config)
    }

    @Provides
    fun provideCartOrderRealmDaoImpl(settingsRepository: SettingsRepository): CartOrderRepository {
        return CartOrderRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideCartRealmDaoImpl(settingsRepository: SettingsRepository): CartRepository {
        return CartRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideOrderRealmDaoImpl(): OrderRepository {
        return OrderRepositoryImpl(config)
    }

    @Provides
    fun provideChargesRealmDaoImpl(): ChargesRepository {
        return ChargesRepositoryImpl(config)
    }

    @Provides
    fun provideAddOnItemRealmDaoImpl(): AddOnItemRepository {
        return AddOnItemRepositoryImpl(config)
    }

    @Provides
    fun providePartnerRealmDaoImpl(): PartnerRepository {
        return PartnerRepositoryImpl(config)
    }

    @Provides
    fun provideEmployeeRealmDaoImpl(): EmployeeRepository {
        return EmployeeRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesCategoryRealmDaoImpl(): ExpensesCategoryRepository {
        return ExpensesCategoryRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesRealmDaoImpl(settingsRepository: SettingsRepository): ExpensesRepository {
        return ExpensesRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideReportsRealmDaoImpl(cartRepository: CartRepository): ReportsRepository {
        return ReportsRepositoryImpl(config, cartRepository)
    }

    @Provides
    fun provideSalaryRealmDaoImpl(): SalaryRepository {
        return SalaryRepositoryImpl(config)
    }

    @Provides
    fun provideAttendanceServiceImpl(): AttendanceRepository {
        return AttendanceRepositoryImpl(config)
    }

    @Provides
    fun provideDataDeletionServiceImpl(settingsRepository: SettingsRepository): DataDeletionRepository {
        return DataDeletionRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideSettingsServiceImpl(): SettingsRepository {
        return SettingsRepositoryImpl(config)
    }
}