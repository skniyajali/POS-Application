package com.niyaj.popos.features.app_settings.di

import android.content.Context
import com.niyaj.popos.features.app_settings.data.repository.BackupRestoreService
import com.niyaj.popos.features.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.app_settings.domain.repository.SettingsValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    @Provides
    fun provideSettingsRepositoryImpl(config : RealmConfiguration) : SettingsRepository {
        return SettingsRepositoryImpl(config)
    }

    @Provides
    fun provideSettingsValidationRepositoryImpl(config : RealmConfiguration) : SettingsValidationRepository {
        return SettingsRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideBackupRestoreService(
        config : RealmConfiguration,
        @ApplicationContext applicationContext : Context
    ): BackupRestoreService {
        return BackupRestoreService(config, applicationContext)
    }
}