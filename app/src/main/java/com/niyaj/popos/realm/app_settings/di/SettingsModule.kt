package com.niyaj.popos.realm.app_settings.di

import com.niyaj.popos.realm.app_settings.data.repository.SettingsRepositoryImpl
import com.niyaj.popos.realm.app_settings.domain.model.Settings
import com.niyaj.popos.realm.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.realm.app_settings.domain.use_cases.GetSetting
import com.niyaj.popos.realm.app_settings.domain.use_cases.SettingsUseCases
import com.niyaj.popos.realm.app_settings.domain.use_cases.UpdateSetting
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsModule {

    private val schema = setOf(Settings::class)

    private val config = RealmConfiguration
        .Builder(schema)
        .deleteRealmIfMigrationNeeded()
        .name("popos.realm")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideSettingsServiceImpl(): SettingsRepository {
        return SettingsRepositoryImpl(config)
    }


    @Provides
    @Singleton
    fun provideSettingsUseCases(settingsRepository: SettingsRepository): SettingsUseCases {
        return SettingsUseCases(
            getSetting = GetSetting(settingsRepository),
            updateSetting = UpdateSetting(settingsRepository)
        )
    }

}