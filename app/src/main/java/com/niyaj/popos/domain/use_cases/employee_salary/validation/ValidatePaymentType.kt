package com.niyaj.popos.domain.use_cases.employee_salary.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidatePaymentType @Inject constructor() {

    fun validate(paymentType: String) : ValidationResult {

        if (paymentType.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Payment type must not be empty."
            )
        }

        return ValidationResult(true)
    }
}