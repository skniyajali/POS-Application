package com.niyaj.popos.features.employee.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.domain.repository.EmployeeValidationRepository
import javax.inject.Inject

class ValidateEmployeeSalary @Inject constructor(
    private val employeeValidationRepository: EmployeeValidationRepository
) {

    operator fun invoke(salary: String): ValidationResult {
        return employeeValidationRepository.validateEmployeeSalary(salary)
    }
}