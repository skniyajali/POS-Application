package com.niyaj.popos.features.profile.di

import com.niyaj.popos.features.profile.data.repository.RestaurantInfoRepositoryImpl
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    fun provideRestaurantInfoRepositoryImpl(config : RealmConfiguration) : RestaurantInfoRepository {
        return RestaurantInfoRepositoryImpl(config)
    }

    @Provides
    fun provideRestaurantInfoValidationRepositoryImpl(config : RealmConfiguration) : RestaurantInfoValidationRepository {
        return RestaurantInfoRepositoryImpl(config)
    }
}