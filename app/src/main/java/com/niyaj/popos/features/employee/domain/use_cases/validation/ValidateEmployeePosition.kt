package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateEmployeePosition @Inject constructor() {

    fun execute(position: String): ValidationResult {

        if (position.isEmpty()){
            return ValidationResult(false, "Employee position is required")
        }
        
        return ValidationResult(true)
    }
}