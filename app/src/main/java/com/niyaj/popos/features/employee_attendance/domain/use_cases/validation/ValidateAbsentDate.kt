package com.niyaj.popos.features.employee_attendance.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import javax.inject.Inject

class ValidateAbsentDate @Inject constructor(
    private val attendanceUseCases: AttendanceUseCases
) {

    fun validate(absentDate: String, employeeId: String? = null, attendanceId: String? = null): ValidationResult {

        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Absent date is required"
            )
        }

        if (employeeId != null) {
            val serverResult = attendanceUseCases.findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)

            if(serverResult){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Selected date already exists.",
                )
            }
        }

        return ValidationResult(true)
    }
}