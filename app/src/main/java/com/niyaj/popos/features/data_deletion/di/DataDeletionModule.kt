package com.niyaj.popos.features.data_deletion.di

import com.niyaj.popos.applicationScope
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.data_deletion.data.repository.DataDeletionRepositoryImpl
import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object DataDeletionModule {

    @Provides
    fun provideDataDeletionRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ) : DataDeletionRepository {
        return DataDeletionRepositoryImpl(config, settingsRepository, applicationScope)
    }
}