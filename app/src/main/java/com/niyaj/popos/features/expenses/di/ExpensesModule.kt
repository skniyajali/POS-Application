package com.niyaj.popos.features.expenses.di

import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.expenses.data.repository.ExpensesRepositoryImpl
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.repository.ExpensesValidationRepository
import com.niyaj.popos.features.expenses.domain.use_cases.GetAllExpenses
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesModule {

    @Provides
    fun provideExpensesRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ) : ExpensesRepository {
        return ExpensesRepositoryImpl(config, settingsRepository)
    }

    @Provides
    fun provideExpensesValidationRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ) : ExpensesValidationRepository {
        return ExpensesRepositoryImpl(config, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideGetAllExpensesUseCases(expensesRepository : ExpensesRepository) : GetAllExpenses {
        return GetAllExpenses(expensesRepository)
    }
}