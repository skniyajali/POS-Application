package com.niyaj.popos.realm.employee.domain.use_cases.validation

import com.niyaj.popos.realm.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateEmployeePhone @Inject constructor(
    private val employeeUseCases: EmployeeUseCases
) {

    fun execute(phone: String, employeeId: String?): ValidationResult{


        if(phone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not be empty",
            )
        }

        if(phone.length != 10){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must be 10(${phone.length}) digits",
            )
        }

        if(phone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone must not contain a letter",
            )
        }

        if(employeeUseCases.findEmployeeByPhone(phone, employeeId)){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no already exists",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }
}