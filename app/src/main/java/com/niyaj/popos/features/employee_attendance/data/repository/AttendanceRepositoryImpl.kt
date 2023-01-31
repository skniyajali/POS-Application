package com.niyaj.popos.features.employee_attendance.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.employee.domain.model.Employee
import com.niyaj.popos.features.employee_attendance.domain.model.EmployeeAttendance
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import com.niyaj.popos.features.employee_attendance.domain.util.AbsentReport
import com.niyaj.popos.util.getSalaryDates
import com.niyaj.popos.util.getStartTime
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
) : AttendanceRepository, AttendanceValidationRepository {

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
            val validateAbsentEmployee = validateAbsentEmployee(attendance.employee?.employeeId ?: "")
            val validateIsAbsent = validateIsAbsent(attendance.isAbsent)
            val validateAbsentDate = validateAbsentDate(
                absentDate = attendance.absentDate,
                employeeId = attendance.employee?.employeeId ?: "",
                attendanceId = attendance.attendeeId
            )

            val hasError = listOf(validateAbsentEmployee, validateIsAbsent, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                val employee = realm.query<Employee>("employeeId == $0", attendance.employee?.employeeId).first().find()

                if (employee != null) {
                    val newAttendance = EmployeeAttendance()
                    newAttendance.attendeeId = attendance.attendeeId.ifEmpty { BsonObjectId().toHexString() }
                    newAttendance.isAbsent = attendance.isAbsent
                    newAttendance.absentDate = attendance.absentDate
                    newAttendance.absentReason = attendance.absentReason
                    newAttendance.createdAt = attendance.createdAt.ifEmpty { getStartTime }

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
            }else {
                Resource.Error("Unable to validate attendance", false)
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
            val validateAbsentEmployee = validateAbsentEmployee(attendance.employee?.employeeId ?: "")
            val validateIsAbsent = validateIsAbsent(attendance.isAbsent)
            val validateAbsentDate = validateAbsentDate(attendance.absentDate,attendance.employee?.employeeId ?: "", attendanceId)

            val hasError = listOf(validateAbsentEmployee, validateIsAbsent, validateAbsentDate).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val employee = realm.query<Employee>("employeeId == $0", attendance.employee?.employeeId).first().find()

                    if (employee != null) {
                        val newAttendance = realm.query<EmployeeAttendance>("attendeeId == $0", attendanceId).first().find()

                        if (newAttendance != null) {
                            realm.write {
                                findLatest(newAttendance)?.apply {
                                    findLatest(employee).also {
                                        this.employee = it
                                    }

                                    this.isAbsent = attendance.isAbsent
                                    this.absentDate = attendance.absentDate
                                    this.absentReason = attendance.absentReason
                                    this.updatedAt = System.currentTimeMillis().toString()
                                }
                            }

                            Resource.Success(true)
                        }else {
                            Resource.Error("Unable to find employee attendance", false)
                        }
                    } else {
                        Resource.Error("Employee not found", false)
                    }
                }
            }else {
                Resource.Error("Unable to validate attendance", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add absent entry.", false)
        }
    }

    override suspend fun removeAttendanceById(attendanceId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val attendance = realm.query<EmployeeAttendance>("attendeeId == $0", attendanceId).first().find()
                if (attendance != null) {
                    realm.write {
                        findLatest(attendance)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find attendance")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance", false)
        }
    }

    override suspend fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val attendance = realm.query<EmployeeAttendance>(
                    "employee.employeeId == $0 AND absentDate == $1",
                    employeeId,
                    date
                ).first().find()

                if (attendance != null) {
                    realm.write {
                        findLatest(attendance)?.let {
                            delete(it)
                        }
                    }

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find attendees", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to remove attendance", false)
        }
    }

    override fun validateAbsentDate(
        absentDate: String,
        employeeId: String?,
        attendanceId: String?
    ): ValidationResult {
        if (absentDate.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Absent date is required"
            )
        }

        if (employeeId != null) {
            val serverResult = findAttendanceByAbsentDate(absentDate, employeeId, attendanceId)

            if(serverResult){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Selected date already exists.",
                )
            }
        }

        return ValidationResult(true)
    }

    override fun validateAbsentEmployee(employeeId: String): ValidationResult {
        if (employeeId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee name must not be empty",
            )
        }

        return ValidationResult(
            successful = true,
        )
    }

    override fun validateIsAbsent(isAbsent: Boolean): ValidationResult {
        if (!isAbsent) {
            return ValidationResult(
                successful = false,
                errorMessage = "Employee must be absent."
            )
        }

        return ValidationResult(true)
    }
}