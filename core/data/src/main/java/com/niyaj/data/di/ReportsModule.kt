package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.ReportsRepositoryImpl
import com.niyaj.data.repository.CartRepository
import com.niyaj.data.repository.ReportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object ReportsModule {

    @Provides
    fun provideReportsRepositoryImpl(
        config: RealmConfiguration,
        cartRepository: CartRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher,
    ): ReportsRepository {
        return ReportsRepositoryImpl(config, cartRepository, ioDispatcher)
    }
}