package com.niyaj.popos.features.customer.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.use_cases.CustomerUseCases
import javax.inject.Inject

class ValidateCustomerPhone @Inject constructor(
    private val customerUseCases: CustomerUseCases
) {

    fun execute(customerPhone: String, customerId: String?): ValidationResult {

        if(customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not be empty",
            )
        }

        if(customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no must be 10 digits",
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if(containsLetters){
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no should not contains any characters"
            )
        }

        if(customerUseCases.findCustomerByPhone(customerPhone, customerId)){
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no already exists"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}