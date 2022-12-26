package com.niyaj.popos.features.profile.domain.use_cases.validation

import android.util.Patterns
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateRestaurantEmail @Inject constructor() {

    fun validate(email: String): ValidationResult {

        if (email.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant email must not be empty"
            )
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant email is not a valid email address.",
            )
        }

        return ValidationResult(true)
    }
}