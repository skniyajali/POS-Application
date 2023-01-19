package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.data.repository.EmployeeValidationRepository
import javax.inject.Inject

class ValidateEmployeePosition @Inject constructor(
    private val employeeValidationRepository: EmployeeValidationRepository
) {

    operator fun invoke(position: String): ValidationResult {
        return employeeValidationRepository.validateEmployeePosition(position)
    }
}