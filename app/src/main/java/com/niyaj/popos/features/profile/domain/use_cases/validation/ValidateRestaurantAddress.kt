package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateRestaurantAddress @Inject constructor() {

    fun validate(address: String): ValidationResult {

        if (address.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant address must not be empty"
            )
        }

        return ValidationResult(true)
    }
}