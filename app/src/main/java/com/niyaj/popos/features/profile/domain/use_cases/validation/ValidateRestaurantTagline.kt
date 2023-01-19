package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import javax.inject.Inject

class ValidateRestaurantTagline @Inject constructor(
    private val restaurantInfoValidationRepository: RestaurantInfoValidationRepository
) {

    operator fun invoke(tagline: String): ValidationResult {
        return restaurantInfoValidationRepository.validateRestaurantTagline(tagline)
    }
}