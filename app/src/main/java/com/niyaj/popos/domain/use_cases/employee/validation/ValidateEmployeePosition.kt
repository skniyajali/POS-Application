package com.niyaj.popos.domain.use_cases.employee.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateEmployeePosition @Inject constructor() {

    fun execute(position: String): ValidationResult {

        if (position.isEmpty()){
            return ValidationResult(false, "Employee position is required")
        }
        
        return ValidationResult(true)
    }
}