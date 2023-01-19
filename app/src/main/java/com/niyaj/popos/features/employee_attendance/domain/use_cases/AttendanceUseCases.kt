package com.niyaj.popos.features.employee_attendance.domain.use_cases

import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentDate
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentEmployee
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateIsAbsent

data class AttendanceUseCases(
    val validateAbsentDate: ValidateAbsentDate,
    val validateAbsentEmployee: ValidateAbsentEmployee,
    val validateIsAbsent: ValidateIsAbsent,
    val getAllAttendance: GetAllAttendance,
    val getAttendanceById: GetAttendanceById,
    val addAbsentEntry: AddAbsentEntry,
    val updateAbsentEntry: UpdateAbsentEntry,
    val deleteAttendanceById: DeleteAttendanceById,
    val deleteAttendanceByEmployeeId: DeleteAttendanceByEmployeeId,
    val getMonthlyAbsentReports: GetMonthlyAbsentReports,
)
