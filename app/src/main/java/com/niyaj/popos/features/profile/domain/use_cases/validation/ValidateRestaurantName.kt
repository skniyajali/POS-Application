package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.profile.domain.repository.RestaurantInfoValidationRepository
import javax.inject.Inject

class ValidateRestaurantName @Inject constructor(
    private val restaurantInfoValidationRepository: RestaurantInfoValidationRepository
) {

   operator fun invoke(name: String): ValidationResult {
        return restaurantInfoValidationRepository.validateRestaurantName(name)
    }
}