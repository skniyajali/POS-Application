package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.RestaurantInfoRepositoryImpl
import com.niyaj.data.repository.RestaurantInfoRepository
import com.niyaj.data.repository.validation.RestaurantInfoValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    fun provideRestaurantInfoRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): RestaurantInfoRepository {
        return RestaurantInfoRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideRestaurantInfoValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): RestaurantInfoValidationRepository {
        return RestaurantInfoRepositoryImpl(config, ioDispatcher)
    }
}