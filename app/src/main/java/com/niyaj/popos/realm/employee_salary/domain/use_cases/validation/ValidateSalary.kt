package com.niyaj.popos.realm.employee_salary.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateSalary @Inject constructor() {

    fun validate(salary: String): ValidationResult {

        if (salary.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not be empty",
            )
        }

        if (salary.length < 2) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must greater than two digits",
            )
        }

        if (salary.any { it.isLetter() }) {
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not contain any characters",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}