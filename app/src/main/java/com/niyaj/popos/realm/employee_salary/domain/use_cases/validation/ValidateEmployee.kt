package com.niyaj.popos.realm.employee_salary.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateEmployee @Inject constructor() {

    fun validate(employeeId: String): ValidationResult {

        if (employeeId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}