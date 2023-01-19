package com.niyaj.popos.features.profile.di

import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoRepository
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import com.niyaj.popos.features.profile.domain.use_cases.GetRestaurantInfo
import com.niyaj.popos.features.profile.domain.use_cases.RestaurantInfoUseCases
import com.niyaj.popos.features.profile.domain.use_cases.UpdateRestaurantInfo
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidatePaymentQrCode
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidatePrimaryPhone
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantAddress
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantEmail
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantName
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantTagline
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateSecondaryPhone
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
    fun provideRestaurantInfoUseCases(
        restaurantInfoRepository: RestaurantInfoRepository,
        restaurantInfoValidationRepository: RestaurantInfoValidationRepository
    ): RestaurantInfoUseCases {
        return RestaurantInfoUseCases(
            getRestaurantInfo = GetRestaurantInfo(restaurantInfoRepository),
            updateRestaurantInfo = UpdateRestaurantInfo(restaurantInfoRepository),
            validateRestaurantAddress = ValidateRestaurantAddress(restaurantInfoValidationRepository),
            validatePaymentQrCode = ValidatePaymentQrCode(restaurantInfoValidationRepository),
            validateRestaurantTagline = ValidateRestaurantTagline(restaurantInfoValidationRepository),
            validateRestaurantEmail = ValidateRestaurantEmail(restaurantInfoValidationRepository),
            validatePrimaryPhone = ValidatePrimaryPhone(restaurantInfoValidationRepository),
            validateSecondaryPhone = ValidateSecondaryPhone(restaurantInfoValidationRepository),
            validateRestaurantName = ValidateRestaurantName(restaurantInfoValidationRepository),
        )
    }
}