package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.PaymentRepositoryImpl
import com.niyaj.data.repository.PaymentRepository
import com.niyaj.data.repository.validation.PaymentValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

/**
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object EmployeeSalaryModule {

    @Provides
    fun provideSalaryRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): PaymentRepository {
        return PaymentRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideSalaryValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): PaymentValidationRepository {
        return PaymentRepositoryImpl(config, ioDispatcher)
    }
}