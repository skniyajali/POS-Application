package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.AbsentReport
import com.niyaj.popos.realm.employee.domain.model.Employee
import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.repository.AttendanceRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee_attendance.AttendanceService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow

class AttendanceRepositoryImpl(private val attendanceService: AttendanceService) : AttendanceRepository {

    override fun getAllAttendance(): Flow<Resource<List<EmployeeAttendance>>> {
        return channelFlow {
            attendanceService.getAllAttendance().collect {result ->
                when (result){
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let {
                            it.map { attendanceRealm ->
                                EmployeeAttendance(
                                    attendeeId = attendanceRealm._id,
                                    isAbsent = attendanceRealm.isAbsent,
                                    absentReason = attendanceRealm.absentReason,
                                    absentDate = attendanceRealm.absentDate,
                                    createdAt = attendanceRealm.created_at,
                                    updatedAt = attendanceRealm.updated_at,
                                    employee = if (attendanceRealm.employee != null)
                                        Employee(
                                            employeeId = attendanceRealm.employee!!.employeeId,
                                            employeeName = attendanceRealm.employee!!.employeeName,
                                            employeePhone = attendanceRealm.employee!!.employeePhone,
                                            employeeSalary = attendanceRealm.employee!!.employeeSalary,
                                            employeeSalaryType = attendanceRealm.employee!!.employeeSalaryType,
                                            employeePosition = attendanceRealm.employee!!.employeePosition,
                                            employeeType = attendanceRealm.employee!!.employeeType,
                                            employeeJoinedDate = attendanceRealm.employee!!.employeeJoinedDate,
                                            createdAt = attendanceRealm.employee!!.createdAt,
                                            updatedAt = attendanceRealm.employee!!.updatedAt
                                        )
                                    else Employee(),
                                )
                            }
                        }


                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get data from database"))
                    }
                }
            }
        }
    }

    override fun getAttendanceById(attendanceId: String): Resource<EmployeeAttendance?> {
        val attendance = attendanceService.getAttendanceById(attendanceId)

        return attendance.data?.let { attendanceRealm ->
            Resource.Success(
                EmployeeAttendance(
                    attendeeId = attendanceRealm._id,
                    isAbsent = attendanceRealm.isAbsent,
                    absentReason = attendanceRealm.absentReason,
                    absentDate = attendanceRealm.absentDate,
                    createdAt = attendanceRealm.created_at,
                    updatedAt = attendanceRealm.updated_at,
                    employee = if (attendanceRealm.employee != null)
                        Employee(
                            employeeId = attendanceRealm.employee!!.employeeId,
                            employeeName = attendanceRealm.employee!!.employeeName,
                            employeePhone = attendanceRealm.employee!!.employeePhone,
                            employeeSalary = attendanceRealm.employee!!.employeeSalary,
                            employeeSalaryType = attendanceRealm.employee!!.employeeSalaryType,
                            employeePosition = attendanceRealm.employee!!.employeePosition,
                            employeeType = attendanceRealm.employee!!.employeeType,
                            employeeJoinedDate = attendanceRealm.employee!!.employeeJoinedDate,
                            createdAt = attendanceRealm.employee!!.createdAt,
                            updatedAt = attendanceRealm.employee!!.updatedAt
                        )
                    else Employee(),
                )
            )
        } ?: Resource.Error(attendance.message ?: "Unable to get data from database")
    }

    override fun findAttendanceByAbsentDate(
        absentDate: String,
        employeeId: String,
        attendanceId: String?,
    ): Boolean {
        return attendanceService.findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)
    }

    override fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean> {
        return attendanceService.addAbsentEntry(attendance)
    }

    override fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReport>>> {
        return channelFlow {
            attendanceService.getMonthlyAbsentReport(employeeId).collect{ result ->
                when (result) {
                    is Resource.Loading -> {
                        send(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        val data = result.data?.let {
                            it.map { attendance ->
                                AbsentReport(
                                    startDate = attendance.startDate,
                                    endDate = attendance.endDate,
                                    absent = attendance.absent.map { absent ->
                                        EmployeeAttendance(
                                            attendeeId = absent._id,
                                            employee = if (absent.employee != null)
                                                Employee(
                                                    employeeId = absent._id,
                                                    employeeName = absent.employee!!.employeeName,
                                                    employeePhone = absent.employee!!.employeePhone,
                                                    employeeSalary = absent.employee!!.employeeSalary,
                                                    employeeSalaryType = absent.employee!!.employeeSalaryType,
                                                    employeePosition = absent.employee!!.employeePosition,
                                                    employeeType = absent.employee!!.employeeType,
                                                    employeeJoinedDate = absent.employee!!.employeeJoinedDate,
                                                    createdAt = absent.employee!!.createdAt,
                                                    updatedAt = absent.employee!!.updatedAt
                                                )
                                            else Employee(),
                                            isAbsent = absent.isAbsent,
                                            absentReason = absent.absentReason,
                                            absentDate = absent.absentDate,
                                            createdAt = absent.created_at,
                                            updatedAt = absent.updated_at
                                        )
                                    }
                                )
                            }
                        }

                        send(Resource.Success(data))
                    }
                    is Resource.Error -> {
                        send(Resource.Error(result.message ?: "Unable to get monthly absent report"))
                    }
                }
            }
        }
    }

    override fun updateAbsentEntry(
        attendanceId: String,
        attendance: EmployeeAttendance,
    ): Resource<Boolean> {
        return attendanceService.updateAbsentEntry(attendanceId, attendance)
    }

    override fun removeAttendanceById(attendanceId: String): Resource<Boolean> {
        return attendanceService.removeAttendanceById(attendanceId)
    }

    override fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean> {
        return attendanceService.removeAttendanceByEmployeeId(employeeId, date)
    }
}