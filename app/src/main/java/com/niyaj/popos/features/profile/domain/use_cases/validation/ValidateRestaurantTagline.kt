package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateRestaurantTagline @Inject constructor() {

    fun validate(tagline: String): ValidationResult {

        if (tagline.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant tagline must not be empty"
            )
        }

        return ValidationResult(true)
    }
}