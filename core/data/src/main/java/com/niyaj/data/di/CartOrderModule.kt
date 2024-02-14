package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CartOrderRepositoryImpl
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): CartOrderRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, ioDispatcher)
    }

    @Provides
    fun provideCartOrderValidationRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): CartOrderValidationRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, ioDispatcher)
    }
}