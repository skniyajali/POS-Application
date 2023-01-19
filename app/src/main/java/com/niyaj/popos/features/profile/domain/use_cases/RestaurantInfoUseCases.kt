package com.niyaj.popos.features.profile.domain.use_cases

import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidatePaymentQrCode
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidatePrimaryPhone
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantAddress
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantEmail
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantName
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateRestaurantTagline
import com.niyaj.popos.features.profile.domain.use_cases.validation.ValidateSecondaryPhone

data class RestaurantInfoUseCases(
    val validateRestaurantAddress: ValidateRestaurantAddress,
    val validatePaymentQrCode: ValidatePaymentQrCode,
    val validateRestaurantTagline: ValidateRestaurantTagline,
    val validateRestaurantEmail: ValidateRestaurantEmail,
    val validatePrimaryPhone: ValidatePrimaryPhone,
    val validateSecondaryPhone: ValidateSecondaryPhone,
    val validateRestaurantName: ValidateRestaurantName,
    val getRestaurantInfo: GetRestaurantInfo,
    val updateRestaurantInfo: UpdateRestaurantInfo
)
