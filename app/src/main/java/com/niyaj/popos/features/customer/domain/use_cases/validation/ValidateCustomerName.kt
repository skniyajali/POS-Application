package com.niyaj.popos.features.customer.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCustomerName @Inject constructor() {

    fun execute(customerName: String?): ValidationResult {

        if(!customerName.isNullOrEmpty()) {
            if(customerName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer name must be 3 characters long",
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}