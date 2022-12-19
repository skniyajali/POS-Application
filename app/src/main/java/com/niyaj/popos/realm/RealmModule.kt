package com.niyaj.popos.realm

import com.niyaj.popos.realm.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.realm.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.realm.address.domain.model.Address
import com.niyaj.popos.realm.address.domain.repository.AddressRepository
import com.niyaj.popos.realm.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart.CartRealmDao
import com.niyaj.popos.realm.cart.CartRealmDaoImpl
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealmDao
import com.niyaj.popos.realm.cart_order.CartOrderRealmDaoImpl
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.category.domain.repository.CategoryRepository
import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import com.niyaj.popos.realm.charges.data.repository.ChargesRepositoryImpl
import com.niyaj.popos.realm.common.CommonRealmDao
import com.niyaj.popos.realm.common.CommonRealmDaoImpl
import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.realm.customer.domain.repository.CustomerRepository
import com.niyaj.popos.realm.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.popos.realm.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.realm.data_deletion.data.repository.DataDeletionRepositoryImpl
import com.niyaj.popos.realm.delivery_partner.domain.model.DeliveryPartner
import com.niyaj.popos.realm.delivery_partner.domain.repository.PartnerRepository
import com.niyaj.popos.realm.delivery_partner.data.repository.PartnerRepositoryImpl
import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.realm.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.realm.employee.data.repository.EmployeeRepositoryImpl
import com.niyaj.popos.realm.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.realm.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.realm.employee_attendance.data.repository.AttendanceRepositoryImpl
import com.niyaj.popos.realm.employee_salary.domain.model.EmployeeSalary
import com.niyaj.popos.realm.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.realm.employee_salary.data.repository.SalaryRepositoryImpl
import com.niyaj.popos.realm.expenses.ExpensesRealm
import com.niyaj.popos.realm.expenses.ExpensesRealmDao
import com.niyaj.popos.realm.expenses.ExpensesRealmDaoImpl
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealm
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealmDao
import com.niyaj.popos.realm.expenses_category.ExpensesCategoryRealmDaoImpl
import com.niyaj.popos.realm.main_feed.MainFeedService
import com.niyaj.popos.realm.main_feed.MainFeedServiceImpl
import com.niyaj.popos.realm.order.OrderRealmDao
import com.niyaj.popos.realm.order.OrderRealmDaoImpl
import com.niyaj.popos.realm.product.ProductRealm
import com.niyaj.popos.realm.product.ProductRealmDao
import com.niyaj.popos.realm.product.ProductRealmDaoImpl
import com.niyaj.popos.realm.reports.ReportsRealm
import com.niyaj.popos.realm.reports.ReportsRealmDao
import com.niyaj.popos.realm.reports.ReportsRealmDaoImpl
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
        ProductRealm::class,
        Customer::class,
        Address::class,
        CartOrderRealm::class,
        CartRealm::class,
        Charges::class,
        AddOnItem::class,
        DeliveryPartner::class,
        Employee::class,
        ExpensesCategoryRealm::class,
        ExpensesRealm::class,
        SelectedCartOrderRealm::class,
        EmployeeSalary::class,
        EmployeeAttendance::class,
        ReportsRealm::class,
    )

    private val config = RealmConfiguration
        .Builder(schema)
        .deleteRealmIfMigrationNeeded()
        .name("popos.realm")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideMainFeedServiceImpl(): MainFeedService {
        return MainFeedServiceImpl(config)
    }

    @Provides
    fun provideCategoryRealmDaoImpl(): CategoryRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    fun provideProductRealmDaoImpl(): ProductRealmDao {
        return ProductRealmDaoImpl(config)
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
    fun provideCartOrderRealmDaoImpl(settingsRepository: SettingsRepository): CartOrderRealmDao {
        return CartOrderRealmDaoImpl(config, settingsRepository)
    }

    @Provides
    fun provideCartRealmDaoImpl(): CartRealmDao {
        return CartRealmDaoImpl(config)
    }

    @Provides
    fun provideOrderRealmDaoImpl(): OrderRealmDao {
        return OrderRealmDaoImpl(config)
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
    fun provideExpensesCategoryRealmDaoImpl(): ExpensesCategoryRealmDao {
        return ExpensesCategoryRealmDaoImpl(config)
    }

    @Provides
    fun provideExpensesRealmDaoImpl(settingsRepository: SettingsRepository): ExpensesRealmDao {
        return ExpensesRealmDaoImpl(config, settingsRepository)
    }

    @Provides
    fun provideCommonRealmDaoImpl(): CommonRealmDao {
        return CommonRealmDaoImpl(config)
    }

    @Provides
    fun provideReportsRealmDaoImpl(cartRealmDao: CartRealmDao): ReportsRealmDao {
        return ReportsRealmDaoImpl(config, cartRealmDao)
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
}