package com.niyaj.popos.features.employee_attendance.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentReport
import com.niyaj.popos.util.getSalaryDates
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AttendanceRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AttendanceRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Attendance Session")
    }

    override suspend fun getAllAttendance(): Flow<Resource<List<EmployeeAttendance>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val attendance =
                        realm.query<EmployeeAttendance>().sort("absentDate", Sort.DESCENDING).find()

                    val items = attendance.asFlow()

                    items.collect { changes: ResultsChange<EmployeeAttendance> ->
                        when (changes) {
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get all Attendance", emptyList()))
                }
            }
        }
    }

    override suspend fun getAttendanceById(attendanceId: String): Resource<EmployeeAttendance?> {
        return try {
            val attendance = withContext(ioDispatcher) {
                realm.query<EmployeeAttendance>("attendeeId == $0", attendanceId).first().find()
            }

            Resource.Success(attendance)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Attendance", null)
        }
    }

    override fun findAttendanceByAbsentDate(
        absentDate: String,
        employeeId: String,
        attendanceId: String?,
    ): Boolean {
        val employeeAttendance = if (attendanceId == null) {
            realm.query<EmployeeAttendance>(
                "absentDate == $0 AND employee.employeeId == $1",
                absentDate,
                employeeId
            ).first().find()
        } else {
            realm.query<EmployeeAttendance>(
                "attendeeId != $0 && absentDate == $1 AND employee.employeeId == $2",
                attendanceId,
                absentDate,
                employeeId
            ).first().find()
        }

        return employeeAttendance != null
    }

    override suspend fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReport>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                withContext(ioDispatcher) {
                    val employee = realm.query<Employee>("employeeId == $0", employeeId).first().find()

                    if (employee != null) {
                        val absentReportRealms = mutableListOf<AbsentReport>()

                        val joinedDate = employee.employeeJoinedDate
                        val dates = getSalaryDates(joinedDate)

                        dates.forEach { date ->
                            if (joinedDate <= date.first) {
                                val reports = mutableListOf<EmployeeAttendance>()

                                val attendances =
                                    realm.query<EmployeeAttendance>(
                                        "employee.employeeId == $0 AND absentDate >= $1 AND absentDate <= $2",
                                        employeeId,
                                        date.first,
                                        date.second
                                    ).sort("absentDate", Sort.DESCENDING).find()

                                attendances.forEach { attendance ->
                                    reports.add(attendance)
                                }

                                absentReportRealms.add(
                                    AbsentReport(
                                        startDate = date.first,
                                        endDate = date.second,
                                        absent = reports
                                    )
                                )
                            }
                        }

                        send(Resource.Success(absentReportRealms))
                        send(Resource.Loading(false))

                    } else {
                        send(Resource.Loading(false))
                        send(Resource.Error("Employee not found", emptyList()))
                    }
                }

            } catch (e: Exception) {
                send(Resource.Loading(false))
                send(Resource.Error(e.message ?: "Unable to get absent reports", emptyList()))
            }
        }
    }

    override suspend fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean> {
        return try {
            val employee =
                realm.query<Employee>("employeeId == $0", attendance.employee?.employeeId).first()
                    .find()

            if (employee != null) {
                val newAttendance = EmployeeAttendance()
                newAttendance.attendeeId = BsonObjectId().toHexString()
                newAttendance.isAbsent = attendance.isAbsent
                newAttendance.absentDate = attendance.absentDate
                newAttendance.absentReason = attendance.absentReason
                newAttendance.createdAt = System.currentTimeMillis().toString()

                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(employee).also {
                            newAttendance.employee = it
                        }

                        this.copyToRealm(newAttendance)
                    }
                }


                Resource.Success(true)
            } else {
                Resource.Error("Employee not found", false)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add absent entry.", false)
        }
    }

    override suspend fun updateAbsentEntry(
        attendanceId: String,
        attendance: EmployeeAttendance,
    ): Resource<Boolean> {
        return try {
            val employee =
                realm.query<Employee>("employeeId == $0", attendance.employee?.employeeId).first()
                    .find()

            if (employee != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        val newAttendance =
                            this.query<EmployeeAttendance>("attendeeId == $0", attendanceId).first()
                                .find()
                        newAttendance?.isAbsent = attendance.isAbsent
                        newAttendance?.absentDate = attendance.absentDate
                        newAttendance?.absentReason = attendance.absentReason
                        newAttendance?.updatedAt = System.currentTimeMillis().toString()

                        findLatest(employee).also {
                            newAttendance?.employee = it
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Employee not found", false)
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add absent entry.", false)
        }
    }

    override suspend fun removeAttendanceById(attendanceId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val attendance =
                        this.query<EmployeeAttendance>("attendeeId == $0", attendanceId).first().find()

                    if (attendance != null) {
                        delete(attendance)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance", false)
        }
    }

    override suspend fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val attendance =
                        this.query<EmployeeAttendance>(
                            "employee.employeeId == $0 AND absentDate == $1",
                            employeeId,
                            date
                        ).first().find()

                    if (attendance != null) {
                        delete(attendance)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance", false)
        }
    }
}