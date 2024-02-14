package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AddressRepositoryImpl
import com.niyaj.data.repository.AddressRepository
import com.niyaj.data.repository.validation.AddressValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object AddressModule {

    @Provides
    fun provideAddressRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): AddressRepository {
        return AddressRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideAddressValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): AddressValidationRepository {
        return AddressRepositoryImpl(config, ioDispatcher)
    }
}