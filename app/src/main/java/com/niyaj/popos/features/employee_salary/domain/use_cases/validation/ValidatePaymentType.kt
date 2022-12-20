package com.niyaj.popos.features.employee_salary.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
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