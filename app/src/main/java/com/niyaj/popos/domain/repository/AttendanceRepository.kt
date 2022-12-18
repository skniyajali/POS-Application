package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.AbsentReport
import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    fun getAllAttendance(): Flow<Resource<List<EmployeeAttendance>>>

    fun getAttendanceById(attendanceId: String): Resource<EmployeeAttendance?>

    fun findAttendanceByAbsentDate(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean

    fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReport>>>

    fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean>

    fun updateAbsentEntry(attendanceId: String, attendance: EmployeeAttendance): Resource<Boolean>

    fun removeAttendanceById(attendanceId: String): Resource<Boolean>

    fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean>
}