package com.niyaj.popos.domain.use_cases.employee_attendance

data class AttendanceUseCases(
    val getAllAttendance: GetAllAttendance,
    val getAttendanceById: GetAttendanceById,
    val findAttendanceByAbsentDate: FindAttendanceByAbsentDate,
    val addAbsentEntry: AddAbsentEntry,
    val updateAbsentEntry: UpdateAbsentEntry,
    val deleteAttendanceById: DeleteAttendanceById,
    val deleteAttendanceByEmployeeId: DeleteAttendanceByEmployeeId,
    val getMonthlyAbsentReports: GetMonthlyAbsentReports,
)
