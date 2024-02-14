package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AddOnItemRepositoryImpl
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {

    @Provides
    fun provideAddOnItemRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): AddOnItemRepository {
        return AddOnItemRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideValidationAddOnItemRepository(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): AddOnItemValidationRepository {
        return AddOnItemRepositoryImpl(config, ioDispatcher)
    }
}