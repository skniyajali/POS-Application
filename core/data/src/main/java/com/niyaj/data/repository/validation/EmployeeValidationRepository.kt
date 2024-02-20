package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface EmployeeValidationRepository {

    suspend fun validateEmployeeName(name: String, employeeId: String? = null): ValidationResult

    suspend fun validateEmployeePhone(phone: String, employeeId: String? = null): ValidationResult

    fun validateEmployeePosition(position: String): ValidationResult

    fun validateEmployeeSalary(salary: String): ValidationResult
}