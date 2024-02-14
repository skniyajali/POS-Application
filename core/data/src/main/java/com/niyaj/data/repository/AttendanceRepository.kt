package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Attendance
import com.niyaj.model.Employee
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    suspend fun getAllEmployee(): Flow<List<Employee>>

    suspend fun getEmployeeById(employeeId: String): Employee?

    suspend fun getAllAttendance(searchText: String): Flow<List<Attendance>>

    suspend fun getAttendanceById(attendanceId: String): Resource<Attendance?>

    fun findAttendanceByAbsentDate(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean

    suspend fun addAbsentEntry(attendance: Attendance): Resource<Boolean>

    suspend fun updateAbsentEntry(attendance: Attendance, attendanceId: String): Resource<Boolean>

    suspend fun removeAttendanceById(attendanceId: String): Resource<Boolean>

    suspend fun removeAttendances(attendanceIds: List<String>): Resource<Boolean>

    suspend fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean>

}