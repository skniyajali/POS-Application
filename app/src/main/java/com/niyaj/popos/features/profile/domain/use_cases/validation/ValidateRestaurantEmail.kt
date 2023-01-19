package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import javax.inject.Inject

class ValidateRestaurantEmail @Inject constructor(
    private val restaurantInfoValidationRepository: RestaurantInfoValidationRepository
) {

    operator fun invoke(email: String): ValidationResult {
        return restaurantInfoValidationRepository.validateRestaurantEmail(email)
    }
}