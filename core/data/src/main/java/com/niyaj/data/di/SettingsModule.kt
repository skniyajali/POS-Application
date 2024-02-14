package com.niyaj.data.di

import android.content.Context
import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.SettingsRepositoryImpl
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.SettingsValidationRepository
import com.niyaj.database.utils.BackupRestoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideSettingsRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): SettingsRepository {
        return SettingsRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideSettingsValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): SettingsValidationRepository {
        return SettingsRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideBackupRestoreService(
        config: RealmConfiguration,
        @ApplicationContext applicationContext: Context
    ): BackupRestoreService {
        return BackupRestoreService(config, applicationContext)
    }
}