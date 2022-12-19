package com.niyaj.popos.realm.employee.domain.use_cases.validation

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