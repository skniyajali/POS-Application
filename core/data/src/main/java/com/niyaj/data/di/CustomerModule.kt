package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.CustomerRepositoryImpl
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Module for providing customer related dependencies
 * @see CustomerRepository
 */
@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {

    @Provides
    fun provideCustomerRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): CustomerRepository {
        return CustomerRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideCustomerValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): CustomerValidationRepository {
        return CustomerRepositoryImpl(config, ioDispatcher)
    }
}