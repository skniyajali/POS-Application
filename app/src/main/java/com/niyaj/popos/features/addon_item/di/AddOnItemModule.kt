package com.niyaj.popos.features.addon_item.di

import com.niyaj.popos.features.addon_item.data.repository.AddOnItemRepositoryImpl
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemValidationRepository
import com.niyaj.popos.features.addon_item.domain.use_cases.GetAllAddOnItems
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AddOnItemModule {

    @Provides
    fun provideAddOnItemRepositoryImpl(config : RealmConfiguration) : AddOnItemRepository {
        return AddOnItemRepositoryImpl(config)
    }

    @Provides
    fun provideValidationAddOnItemRepository(config : RealmConfiguration) : AddOnItemValidationRepository {
        return AddOnItemRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideGetAllAddOnItemsUseCases(
        addOnItemRepository : AddOnItemRepository,
    ) : GetAllAddOnItems {
        return GetAllAddOnItems(addOnItemRepository)
    }

}