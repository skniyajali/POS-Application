package com.niyaj.popos.realm.employee_attendance.domain.use_cases

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
