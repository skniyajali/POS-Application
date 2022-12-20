package com.niyaj.popos.features.customer.domain.use_cases.validation

import android.util.Patterns
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCustomerEmail @Inject constructor() {

    fun execute(customerEmail: String?): ValidationResult {

        if(!customerEmail.isNullOrEmpty()) {
            if(!Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer email is not a valid email address.",
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}