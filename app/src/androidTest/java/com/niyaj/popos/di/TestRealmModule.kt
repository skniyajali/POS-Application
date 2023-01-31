package com.niyaj.popos.di

import com.niyaj.popos.applicationScope
import com.niyaj.popos.features.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.address.data.repository.AddressRepositoryImpl
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.address.domain.repository.AddressValidationRepository
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.app_settings.domain.repository.SettingsValidationRepository
import com.niyaj.popos.features.cart.data.repository.CartRepositoryImpl
import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart_order.data.repository.CartOrderRepositoryImpl
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.charges.data.repository.ChargesRepositoryImpl
import com.niyaj.popos.features.charges.domain.repository.ChargesRepository
import com.niyaj.popos.features.charges.domain.repository.ChargesValidationRepository
import com.niyaj.popos.features.customer.data.repository.CustomerRepositoryImpl
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import com.niyaj.popos.features.data_deletion.data.repository.DataDeletionRepositoryImpl
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.features.employee.data.repository.EmployeeRepositoryImpl
import com.niyaj.popos.features.employee.domain.repository.EmployeeValidationRepository
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee_attendance.data.repository.AttendanceRepositoryImpl
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import com.niyaj.popos.features.employee_salary.data.repository.SalaryRepositoryImpl
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryValidationRepository
import com.niyaj.popos.features.expenses.data.repository.ExpensesRepositoryImpl
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import com.niyaj.popos.features.expenses_category.data.repository.ExpensesCategoryRepositoryImpl
import com.niyaj.popos.features.expenses_category.domain.repository.ExpCategoryValidationRepository
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.features.main_feed.data.repository.MainFeedRepositoryImpl
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.order.data.repository.OrderRepositoryImpl
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.features.product.data.repository.ProductRepositoryImpl
import com.niyaj.popos.features.product.domain.repository.ProductRepository
import com.niyaj.popos.features.product.domain.repository.ProductValidationRepository
import com.niyaj.popos.features.profile.data.repository.RestaurantInfoRepositoryImpl
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import com.niyaj.popos.features.reports.data.repository.ReportsRepositoryImpl
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestRealmModule {
    private val config = TestConfig.config()

    @Provides
    @Singleton
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined

    @Provides
    fun provideCategoryRepositoryImpl(): CategoryRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    fun provideCategoryValidationRepositoryImpl(): CategoryValidationRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    fun provideProductRepositoryImpl(): ProductRepository {
        return ProductRepositoryImpl(config)
    }

    @Provides
    fun provideProductValidationRepositoryImpl(): ProductValidationRepository {
        return ProductRepositoryImpl(config)
    }

    @Provides
    fun provideCustomerRepositoryImpl(): CustomerRepository {
        return CustomerRepositoryImpl(config)
    }

    @Provides
    fun provideCustomerValidationRepositoryImpl(): CustomerValidationRepository {
        return CustomerRepositoryImpl(config)
    }

    @Provides
    fun provideAddressRepositoryImpl(): AddressRepository {
        return AddressRepositoryImpl(config)
    }

    @Provides
    fun provideAddressValidationRepositoryImpl(): AddressValidationRepository {
        return AddressRepositoryImpl(config)
    }

    @Provides
    fun provideCartOrderRepositoryImpl(settingsRepository: SettingsRepository): CartOrderRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, applicationScope)
    }

    @Provides
    fun provideCartOrderValidationRepositoryImpl(settingsRepository: SettingsRepository): CartOrderValidationRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, applicationScope)
    }

    @Provides
    fun provideCartRepositoryImpl(settingsRepository: SettingsRepository): CartRepository {
        return CartRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideOrderRepositoryImpl(): OrderRepository {
        return OrderRepositoryImpl(config)
    }

    @Provides
    fun provideChargesRepositoryImpl(): ChargesRepository {
        return ChargesRepositoryImpl(config)
    }

    @Provides
    fun provideChargesValidationRepositoryImpl(): ChargesValidationRepository {
        return ChargesRepositoryImpl(config)
    }

    @Provides
    fun provideAddOnItemRepositoryImpl(): AddOnItemRepository {
        return AddOnItemRepositoryImpl(config)
    }

    @Provides
    fun provideValidationAddOnItemRepository(): ValidationRepository {
        return AddOnItemRepositoryImpl(config)
    }

    @Provides
    fun provideEmployeeRepositoryImpl(): EmployeeRepository {
        return EmployeeRepositoryImpl(config)
    }

    @Provides
    fun provideEmployeeValidationRepositoryImpl(): EmployeeValidationRepository {
        return EmployeeRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesCategoryRepositoryImpl(): ExpensesCategoryRepository {
        return ExpensesCategoryRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesCategoryValidationRepositoryImpl(): ExpCategoryValidationRepository {
        return ExpensesCategoryRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesRepositoryImpl(settingsRepository: SettingsRepository): ExpensesRepository {
        return ExpensesRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideExpensesValidationRepositoryImpl(settingsRepository: SettingsRepository): ExpensesValidationRepository {
        return ExpensesRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideReportsRepositoryImpl(cartRepository: CartRepository): ReportsRepository {
        return ReportsRepositoryImpl(config, cartRepository)
    }

    @Provides
    fun provideSalaryRepositoryImpl(): SalaryRepository {
        return SalaryRepositoryImpl(config)
    }

    @Provides
    fun provideSalaryValidationRepositoryImpl(): SalaryValidationRepository {
        return SalaryRepositoryImpl(config)
    }

    @Provides
    fun provideAttendanceRepositoryImpl(): AttendanceRepository {
        return AttendanceRepositoryImpl(config)
    }

    @Provides
    fun provideAttendanceValidationRepositoryImpl(): AttendanceValidationRepository {
        return AttendanceRepositoryImpl(config)
    }

    @Provides
    fun provideDataDeletionRepositoryImpl(settingsRepository: SettingsRepository): DataDeletionRepository {
        return DataDeletionRepositoryImpl(config, settingsRepository, applicationScope)
    }

    @Provides
    fun provideSettingsRepositoryImpl(): SettingsRepository {
        return SettingsRepositoryImpl(config)
    }

    @Provides
    fun provideSettingsValidationRepositoryImpl(): SettingsValidationRepository {
        return SettingsRepositoryImpl(config)
    }

    @Provides
    fun provideMainFeedRepositoryImpl(): MainFeedRepository {
        return MainFeedRepositoryImpl(config)
    }

    @Provides
    fun provideRestaurantInfoRepositoryImpl(): RestaurantInfoRepository {
        return RestaurantInfoRepositoryImpl(config)
    }

    @Provides
    fun provideRestaurantInfoValidationRepositoryImpl(): RestaurantInfoValidationRepository {
        return RestaurantInfoRepositoryImpl(config)
    }
}