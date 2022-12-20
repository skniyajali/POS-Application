package com.niyaj.popos.features.employee_salary.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateSalaryNote @Inject constructor() {

    fun validate(salaryNote: String, isRequired: Boolean = false): ValidationResult {

        if (isRequired) {
            if (salaryNote.isEmpty()){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Salary note required."
                )
            }
        }

        return ValidationResult(true)
    }
}