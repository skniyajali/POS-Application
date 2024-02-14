package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.HomeRepositoryImpl
import com.niyaj.data.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object MainFeedModule {

    @Provides
    fun provideMainFeedRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): HomeRepository {
        return HomeRepositoryImpl(config, ioDispatcher)
    }
}