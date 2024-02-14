package com.niyaj.database.di

import com.niyaj.database.model.AccountEntity
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CategoryEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.database.model.PaymentEntity
import com.niyaj.database.model.ExpensesEntity
import com.niyaj.database.model.ExpensesCategoryEntity
import com.niyaj.database.model.PrinterEntity
import com.niyaj.database.model.ProductEntity
import com.niyaj.database.model.ReminderEntity
import com.niyaj.database.model.ReportsEntity
import com.niyaj.database.model.RestaurantInfoEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.SettingsEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object RealmModule {

    private val schema = setOf(
        ProductEntity::class,
        CategoryEntity::class,
        CustomerEntity::class,
        AddressEntity::class,
        CartOrderEntity::class,
        SelectedCartOrderEntity::class,
        CartEntity::class,
        ChargesEntity::class,
        AddOnItemEntity::class,
        ExpensesCategoryEntity::class,
        ExpensesEntity::class,
        EmployeeEntity::class,
        PaymentEntity::class,
        AttendanceEntity::class,
        ReportsEntity::class,
        SettingsEntity::class,
        RestaurantInfoEntity::class,
        ReminderEntity::class,
        AccountEntity::class,
        PrinterEntity::class,
    )

    @Provides
    fun provideRealmConfig(): RealmConfiguration {
        return RealmConfiguration
            .Builder(schema)
            .name("popos.realm")
            .schemaVersion(2)
            .build()
    }
}