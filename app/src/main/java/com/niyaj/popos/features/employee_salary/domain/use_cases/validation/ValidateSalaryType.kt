package com.niyaj.popos.features.employee_salary.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateSalaryType @Inject constructor() {

    fun validate(salaryType: String): ValidationResult {

        if (salaryType.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary type must not be empty",
            )
        }

        return ValidationResult(true)
    }
}