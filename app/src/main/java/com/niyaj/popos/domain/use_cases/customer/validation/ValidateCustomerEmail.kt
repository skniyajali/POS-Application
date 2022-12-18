package com.niyaj.popos.domain.use_cases.customer.validation

import android.util.Patterns
import com.niyaj.popos.domain.util.ValidationResult
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