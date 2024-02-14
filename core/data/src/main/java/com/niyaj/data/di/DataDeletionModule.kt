package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.DataDeletionRepositoryImpl
import com.niyaj.data.repository.DataDeletionRepository
import com.niyaj.data.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DataDeletionModule {

    @Provides
    fun provideDataDeletionRepositoryImpl(
        config: RealmConfiguration,
        settingsRepository: SettingsRepository,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): DataDeletionRepository {
        return DataDeletionRepositoryImpl(config, settingsRepository, ioDispatcher)
    }
}