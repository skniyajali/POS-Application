package com.niyaj.popos.realm.employee_attendance

import com.niyaj.popos.domain.model.AbsentReportRealm
import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceService {

    fun getAllAttendance(): Flow<Resource<List<AttendanceRealm>>>

    fun getAttendanceById(attendanceId: String): Resource<AttendanceRealm?>

    fun findAttendanceByAbsentDate(absentDate: String, employeeId: String, attendanceId: String? = null): Boolean

    fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReportRealm>>>

    fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean>

    fun updateAbsentEntry(attendanceId: String, attendance: EmployeeAttendance): Resource<Boolean>

    fun removeAttendanceById(attendanceId: String): Resource<Boolean>

    fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean>

}