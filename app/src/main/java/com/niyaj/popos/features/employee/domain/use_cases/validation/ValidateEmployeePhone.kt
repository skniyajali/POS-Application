package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.data.repository.EmployeeValidationRepository
import javax.inject.Inject

class ValidateEmployeePhone @Inject constructor(
    private val employeeValidationRepository: EmployeeValidationRepository
) {

    operator fun invoke(phone: String, employeeId: String?): ValidationResult {
        return employeeValidationRepository.validateEmployeePhone(phone, employeeId)
    }
}