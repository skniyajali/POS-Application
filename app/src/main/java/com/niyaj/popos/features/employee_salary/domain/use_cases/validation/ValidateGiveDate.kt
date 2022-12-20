package com.niyaj.popos.features.employee_salary.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateGiveDate @Inject constructor() {

    fun validate(givenDate: String): ValidationResult {

        if (givenDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Given date must not be empty",
            )
        }


        return ValidationResult(
            successful = true,
        )
    }
}