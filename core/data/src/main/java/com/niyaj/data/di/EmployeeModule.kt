package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.EmployeeRepositoryImpl
import com.niyaj.data.repository.EmployeeRepository
import com.niyaj.data.repository.validation.EmployeeValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Module that provides the [EmployeeRepository] use case.
 * @see [EmployeeRepository]
 */
@Module
@InstallIn(SingletonComponent::class)
object EmployeeModule {

    @Provides
    fun provideEmployeeRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): EmployeeRepository {
        return EmployeeRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideEmployeeValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): EmployeeValidationRepository {
        return EmployeeRepositoryImpl(config, ioDispatcher)
    }
}