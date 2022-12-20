package com.niyaj.popos.features.data_deletion.di

import com.niyaj.popos.features.data_deletion.domain.repository.DataDeletionRepository
import com.niyaj.popos.features.data_deletion.domain.use_cases.DataDeletionUseCases
import com.niyaj.popos.features.data_deletion.domain.use_cases.DeleteAllRecords
import com.niyaj.popos.features.data_deletion.domain.use_cases.DeleteData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDeletionModule {


    @Provides
    @Singleton
    fun provideDataDeletionUseCases(dataDeletionRepository: DataDeletionRepository): DataDeletionUseCases {
        return DataDeletionUseCases(
            deleteData = DeleteData(dataDeletionRepository),
            deleteAllRecords = DeleteAllRecords(dataDeletionRepository),
        )
    }
}