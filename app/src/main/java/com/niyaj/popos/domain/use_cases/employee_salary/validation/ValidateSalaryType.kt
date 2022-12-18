package com.niyaj.popos.domain.use_cases.employee_salary.validation

import com.niyaj.popos.domain.util.SalaryType
import com.niyaj.popos.domain.util.ValidationResult
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