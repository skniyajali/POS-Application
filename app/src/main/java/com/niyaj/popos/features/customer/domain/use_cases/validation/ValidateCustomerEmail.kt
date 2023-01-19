package com.niyaj.popos.features.customer.domain.use_cases.validation

import android.util.Patterns
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import javax.inject.Inject

class ValidateCustomerEmail @Inject constructor(
    private val customerValidationRepository: CustomerValidationRepository
) {

    operator fun invoke(customerEmail: String?): ValidationResult {
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