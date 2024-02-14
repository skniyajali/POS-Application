package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ExpensesRepositoryImpl
import com.niyaj.data.repository.ExpensesRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.ExpensesValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ExpensesModule {

    @Provides
    fun provideExpensesRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ) : ExpensesRepository {
        return ExpensesRepositoryImpl(config, settingsRepository, ioDispatcher)
    }

    @Provides
    fun provideExpensesValidationRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ) : ExpensesValidationRepository {
        return ExpensesRepositoryImpl(config, settingsRepository, ioDispatcher)
    }
}