package com.niyaj.popos.features.employee_attendance.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentReport
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    suspend fun getAllAttendance(): Flow<Resource<List<EmployeeAttendance>>>

    suspend fun getAttendanceById(attendanceId: String): Resource<EmployeeAttendance?>

    fun findAttendanceByAbsentDate(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean

    suspend fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReport>>>

    suspend fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean>

    suspend fun updateAbsentEntry(attendanceId: String, attendance: EmployeeAttendance): Resource<Boolean>

    suspend fun removeAttendanceById(attendanceId: String): Resource<Boolean>

    suspend fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean>

}