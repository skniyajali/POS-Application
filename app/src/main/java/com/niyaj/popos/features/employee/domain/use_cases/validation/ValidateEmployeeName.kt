package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.domain.repository.EmployeeValidationRepository
import javax.inject.Inject

class ValidateEmployeeName @Inject constructor(
    private val employeeValidationRepository: EmployeeValidationRepository
) {

    operator fun invoke(name: String, employeeId: String?): ValidationResult {
        return employeeValidationRepository.validateEmployeeName(name, employeeId)
    }
}