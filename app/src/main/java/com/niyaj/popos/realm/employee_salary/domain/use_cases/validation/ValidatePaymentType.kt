package com.niyaj.popos.realm.employee_salary.domain.use_cases.validation

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