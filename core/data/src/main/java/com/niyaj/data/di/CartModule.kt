package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CartRepositoryImpl
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    fun provideCartRepositoryImpl(
        config: RealmConfiguration,
        settingsRepository: SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): CartRepository {
        return CartRepositoryImpl(config, settingsRepository, ioDispatcher)
    }
}