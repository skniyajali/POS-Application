package com.niyaj.popos.features.employee_attendance.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface AttendanceValidationRepository {

    fun validateAbsentDate(absentDate: String, employeeId: String? = null, attendanceId: String? = null): ValidationResult

    fun validateAbsentEmployee(employeeId: String): ValidationResult

    fun validateIsAbsent(isAbsent: Boolean): ValidationResult
}