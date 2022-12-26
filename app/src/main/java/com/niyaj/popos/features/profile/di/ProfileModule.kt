package com.niyaj.popos.features.profile.di

import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.features.profile.domain.use_cases.GetRestaurantInfo
import com.niyaj.popos.features.profile.domain.use_cases.RestaurantInfoUseCases
import com.niyaj.popos.features.profile.domain.use_cases.UpdateRestaurantInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideRestaurantInfoUseCases(restaurantInfoRepository: RestaurantInfoRepository): RestaurantInfoUseCases {
        return RestaurantInfoUseCases(
            getRestaurantInfo = GetRestaurantInfo(restaurantInfoRepository),
            updateRestaurantInfo = UpdateRestaurantInfo(restaurantInfoRepository)
        )
    }
}