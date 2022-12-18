package com.niyaj.popos.domain.use_cases.employee.validation

import com.niyaj.popos.domain.use_cases.employee.EmployeeUseCases
import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateEmployeeName @Inject constructor(
    private val employeeUseCases: EmployeeUseCases
) {

    fun execute(name: String, employeeId: String?): ValidationResult {

        if(name.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        if(name.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not contain any digit",
            )
        }

        if(name.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must be more than 4 characters",
            )
        }

        if(employeeUseCases.findEmployeeByName(name, employeeId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name already exists.",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}