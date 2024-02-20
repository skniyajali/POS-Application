package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface AttendanceValidationRepository {

    suspend fun validateAbsentDate(
        absentDate: String,
        employeeId: String? = null,
        attendanceId: String? = null
    ): ValidationResult

    fun validateAbsentEmployee(employeeId: String): ValidationResult

    fun validateIsAbsent(isAbsent: Boolean): ValidationResult
}