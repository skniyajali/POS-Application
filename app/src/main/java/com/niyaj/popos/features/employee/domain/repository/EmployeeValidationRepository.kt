package com.niyaj.popos.features.employee.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface EmployeeValidationRepository {

    fun validateEmployeeName(name: String, employeeId: String? = null): ValidationResult

    fun validateEmployeePhone(phone: String, employeeId: String? = null): ValidationResult

    fun validateEmployeePosition(position: String): ValidationResult

    fun validateEmployeeSalary(salary: String): ValidationResult
}