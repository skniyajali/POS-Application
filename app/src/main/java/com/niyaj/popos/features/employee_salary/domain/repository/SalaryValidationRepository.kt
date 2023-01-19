package com.niyaj.popos.features.employee_salary.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface SalaryValidationRepository {

    fun validateEmployee(employeeId: String): ValidationResult

    fun validateGivenDate(givenDate: String): ValidationResult

    fun validatePaymentType(paymentType: String) : ValidationResult

    fun validateSalary(salary: String): ValidationResult

    fun validateSalaryNote(salaryNote: String, isRequired: Boolean = false): ValidationResult

    fun validateSalaryType(salaryType: String): ValidationResult
}