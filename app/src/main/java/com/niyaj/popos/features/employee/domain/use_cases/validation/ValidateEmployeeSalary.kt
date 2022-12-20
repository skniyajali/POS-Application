package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateEmployeeSalary @Inject constructor() {

    fun execute(salary: String): ValidationResult {

        if (salary.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Salary must not be empty",
            )
        }

        if(salary.length > 5){
            return ValidationResult(
                successful = false,
                errorMessage = "Salary is in invalid",
            )
        }

        if(salary.any { it.isLetter() }){
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