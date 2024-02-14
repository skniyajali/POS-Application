package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ExpensesCategoryRepositoryImpl
import com.niyaj.data.repository.ExpensesCategoryRepository
import com.niyaj.data.repository.validation.ExpCategoryValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ExpensesCategoryModule {

    @Provides
    fun provideExpensesCategoryRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): ExpensesCategoryRepository {
        return ExpensesCategoryRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideExpensesCategoryValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): ExpCategoryValidationRepository {
        return ExpensesCategoryRepositoryImpl(config, ioDispatcher)
    }
}