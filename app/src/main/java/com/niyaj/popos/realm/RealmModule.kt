package com.niyaj.popos.realm

import com.niyaj.popos.realm.add_on_items.AddOnItemRealm
import com.niyaj.popos.realm.add_on_items.AddOnItemRealmDao
import com.niyaj.popos.realm.add_on_items.AddOnItemRealmDaoImpl
import com.niyaj.popos.realm.address.AddressRealm
import com.niyaj.popos.realm.address.AddressRealmDao
import com.niyaj.popos.realm.address.AddressRealmDaoImpl
import com.niyaj.popos.realm.app_settings.SettingsRealm
import com.niyaj.popos.realm.app_settings.SettingsService
import com.niyaj.popos.realm.app_settings.SettingsServiceImpl
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart.CartRealmDao
import com.niyaj.popos.realm.cart.CartRealmDaoImpl
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealmDao
import com.niyaj.popos.realm.cart_order.CartOrderRealmDaoImpl
import com.niyaj.popos.realm.cart_order.SelectedCartOrderRealm
import com.niyaj.popos.realm.category.CategoryRealm
import com.niyaj.popos.realm.category.CategoryRealmDao
import com.niyaj.popos.realm.category.CategoryRealmDaoImpl
import com.niyaj.popos.realm.charges.ChargesRealm
import com.niyaj.popos.realm.charges.ChargesRealmDao
import com.niyaj.popos.realm.charges.ChargesRealmDaoImpl
import com.niyaj.popos.realm.common.CommonRealmDao
import com.niyaj.popos.realm.common.CommonRealmDaoImpl
import com.niyaj.popos.realm.customer.CustomerRealm
import com.niyaj.popos.realm.customer.CustomerRealmDao
import com.niyaj.popos.realm.customer.CustomerRealmDaoImpl
import com.niyaj.popos.realm.data_deletion.DataDeletionService
import com.niyaj.popos.realm.data_deletion.DataDeletionServiceImpl
import com.niyaj.popos.realm.delivery_partner.PartnerRealm
import com.niyaj.popos.realm.delivery_partner.PartnerRealmDao
import com.niyaj.popos.realm.delivery_partner.PartnerRealmDaoImpl
import com.niyaj.popos.realm.employee.EmployeeRealm
import com.niyaj.popos.realm.employee.EmployeeRealmDao
import com.niyaj.popos.realm.employee.EmployeeRealmDaoImpl
import com.niyaj.popos.realm.employee_attendance.AttendanceRealm
import com.niyaj.popos.realm.employee_attendance.AttendanceService
import com.niyaj.popos.realm.employee_attendance.AttendanceServiceImpl
import com.niyaj.popos.realm.employee_salary.SalaryRealm
import com.niyaj.popos.realm.employee_salary.SalaryRealmDao
import com.niyaj.popos.realm.employee_salary.SalaryRealmDaoImpl
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
import com.niyaj.popos.realmApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.User
import io.realm.kotlin.mongodb.sync.SyncConfiguration

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    private val user: User? = realmApp.currentUser

    private val schema = setOf(
        CategoryRealm::class,
        ProductRealm::class,
        AddressRealm::class,
        CustomerRealm::class,
        CartOrderRealm::class,
        CartRealm::class,
        AddOnItemRealm::class,
        ChargesRealm::class,
        PartnerRealm::class,
        EmployeeRealm::class,
        ExpensesCategoryRealm::class,
        ExpensesRealm::class,
        SelectedCartOrderRealm::class,
        SalaryRealm::class,
        AttendanceRealm::class,
        ReportsRealm::class,
        SettingsRealm::class,
    )

    private val config = SyncConfiguration
        .Builder(user!!, schema)
        .initialSubscriptions{ realm ->
            this.add(
                query = realm.query<CategoryRealm>(),
                name = "category",
                updateExisting = true,
            )

            this.add(
                query = realm.query<ProductRealm>(),
                name = "product",
                updateExisting = true,
            )

            this.add(
                query = realm.query<AddressRealm>(),
                name = "address",
                updateExisting = true,
            )

            this.add(
                query = realm.query<CustomerRealm>(),
                name = "customer",
                updateExisting = true,
            )

            this.add(
                query = realm.query<CartOrderRealm>(),
                name = "cartOrder",
                updateExisting = true,
            )

            this.add(
                query = realm.query<CartRealm>(),
                name = "cartProducts",
                updateExisting = true,
            )

            this.add(
                query = realm.query<AddOnItemRealm>(),
                name = "addOnItems",
                updateExisting = true,
            )

            this.add(
                query = realm.query<ChargesRealm>(),
                name = "charges",
                updateExisting = true,
            )

            this.add(
                query = realm.query<PartnerRealm>(),
                name = "delivery_partner",
                updateExisting = true,
            )

            this.add(
                query = realm.query<EmployeeRealm>(),
                name = "employee",
                updateExisting = true,
            )

            this.add(
                query = realm.query<ExpensesCategoryRealm>(),
                name = "expenses_category",
                updateExisting = true,
            )

            this.add(
                query = realm.query<ExpensesRealm>(),
                name = "expenses",
                updateExisting = true,
            )

            this.add(
                query = realm.query<SelectedCartOrderRealm>(),
                name = "selectedCartOrder",
                updateExisting = true,
            )

            this.add(
                query = realm.query<SalaryRealm>(),
                name = "salary",
                updateExisting = true,
            )

            this.add(
                query = realm.query<AttendanceRealm>(),
                name = "attendance",
                updateExisting = true,
            )

            this.add(
                query = realm.query<ReportsRealm>(),
                name = "reports",
                updateExisting = true,
            )

            this.add(
                query = realm.query<SettingsRealm>(),
                name = "settings",
                updateExisting = true,
            )
        }
        .waitForInitialRemoteData()
        .name("popos")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideMainFeedServiceImpl(): MainFeedService {
        return MainFeedServiceImpl(config)
    }

    @Provides
    fun provideCategoryRealmDaoImpl(): CategoryRealmDao {
        return CategoryRealmDaoImpl(config)
    }

    @Provides
    fun provideProductRealmDaoImpl(): ProductRealmDao {
        return ProductRealmDaoImpl(config)
    }

    @Provides
    fun provideAddressRealmDaoImpl(): AddressRealmDao {
        return AddressRealmDaoImpl(config)
    }

    @Provides
    fun provideCustomerRealmDaoImpl(): CustomerRealmDao {
        return CustomerRealmDaoImpl(config)
    }

    @Provides
    fun provideCartOrderRealmDaoImpl(settingsService: SettingsService): CartOrderRealmDao {
        return CartOrderRealmDaoImpl(config, settingsService)
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
    fun provideAddOnItemRealmDaoImpl(): AddOnItemRealmDao {
        return AddOnItemRealmDaoImpl(config)
    }

    @Provides
    fun provideChargesRealmDaoImpl(): ChargesRealmDao {
        return ChargesRealmDaoImpl(config)
    }

    @Provides
    fun providePartnerRealmDaoImpl(): PartnerRealmDao {
        return PartnerRealmDaoImpl(config)
    }

    @Provides
    fun provideEmployeeRealmDaoImpl(): EmployeeRealmDao {
        return EmployeeRealmDaoImpl(config)
    }

    @Provides
    fun provideExpensesCategoryRealmDaoImpl(): ExpensesCategoryRealmDao {
        return ExpensesCategoryRealmDaoImpl(config)
    }

    @Provides
    fun provideExpensesRealmDaoImpl(settingsService: SettingsService): ExpensesRealmDao {
        return ExpensesRealmDaoImpl(config, settingsService)
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
    fun provideSalaryRealmDaoImpl(): SalaryRealmDao {
        return SalaryRealmDaoImpl(config)
    }

    @Provides
    fun provideAttendanceServiceImpl(): AttendanceService {
        return AttendanceServiceImpl(config)
    }

    @Provides
    fun provideDataDeletionServiceImpl(settingsService: SettingsService): DataDeletionService {
        return DataDeletionServiceImpl(config, settingsService)
    }

    @Provides
    fun provideSettingsServiceImpl(): SettingsService {
        return SettingsServiceImpl(config)
    }
}