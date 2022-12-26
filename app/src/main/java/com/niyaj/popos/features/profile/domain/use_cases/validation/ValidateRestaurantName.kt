package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateRestaurantName @Inject constructor() {

    fun validate(name: String): ValidationResult {

        if (name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant name must not be empty"
            )
        }

        return ValidationResult(true)
    }
}