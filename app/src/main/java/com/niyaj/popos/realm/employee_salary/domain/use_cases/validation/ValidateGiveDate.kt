package com.niyaj.popos.realm.employee_salary.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
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