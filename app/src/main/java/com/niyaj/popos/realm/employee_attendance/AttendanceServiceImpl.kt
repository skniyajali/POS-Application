package com.niyaj.popos.realm.employee_attendance

import com.niyaj.popos.domain.model.AbsentReportRealm
import com.niyaj.popos.domain.model.EmployeeAttendance
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.employee.EmployeeRealm
import com.niyaj.popos.realmApp
import com.niyaj.popos.util.getSalaryDates
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AttendanceServiceImpl(config: SyncConfiguration) : AttendanceService {

    private val user = realmApp.currentUser

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if (user == null && sessionState != "ACTIVE") {
            Timber.d("Attendance: user is null")
        }

        Timber.d("Attendance Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }


    override fun getAllAttendance(): Flow<Resource<List<AttendanceRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val attendance = realm.query<AttendanceRealm>().sort("absentDate", Sort.DESCENDING).find().asFlow()

                attendance.collect { changes: ResultsChange<AttendanceRealm> ->
                    when(changes) {
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
            }catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get all Attendance"))
            }
        }
    }

    override fun getAttendanceById(attendanceId: String): Resource<AttendanceRealm?> {
        return try {
            val attendance = realm.query<AttendanceRealm>("_id == $0", attendanceId).first().find()
            
            Resource.Success(attendance)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Attendance", null)
        }
    }

    override fun findAttendanceByAbsentDate(
        absentDate: String,
        employeeId: String,
        attendanceId: String?,
    ): Boolean {
        val attendanceRealm = if(attendanceId == null) {
            realm.query<AttendanceRealm>("absentDate == $0 AND employee._id == $1", absentDate, employeeId).first().find()
        }else {
            realm.query<AttendanceRealm>("_id != $0 && absentDate == $1 AND employee._id == $2", attendanceId, absentDate, employeeId).first().find()
        }

        return attendanceRealm != null
    }

    override fun getMonthlyAbsentReport(employeeId: String): Flow<Resource<List<AbsentReportRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))
                val employee = realm.query<EmployeeRealm>("_id == $0", employeeId).first().find()

                if (employee != null) {
                    val absentReportRealms = mutableListOf<AbsentReportRealm>()

                    val joinedDate = employee.employeeJoinedDate
                    val dates = getSalaryDates(joinedDate)

                    dates.forEach { date ->
                        if (joinedDate <= date.first) {
                            val reports = mutableListOf<AttendanceRealm>()

                            val attendances =
                                realm.query<AttendanceRealm>("employee._id == $0 AND absentDate >= $1 AND absentDate <= $2",
                                    employeeId,
                                    date.first,
                                    date.second).sort("absentDate", Sort.DESCENDING).find()

                            attendances.forEach { attendance ->
                                reports.add(attendance)
                            }

                            absentReportRealms.add(
                                AbsentReportRealm(
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
                    send(Resource.Error("Employee not found"))
                }

            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get absent reports"))
            }
        }
    }

    override fun addAbsentEntry(attendance: EmployeeAttendance): Resource<Boolean> {
        return if (user != null) {
            try {

                val employee =
                    realm.query<EmployeeRealm>("_id == $0", attendance.employee.employeeId).first()
                        .find()

                if (employee != null) {
                    val newAttendance = AttendanceRealm(user.id)
                    newAttendance.isAbsent = attendance.isAbsent
                    newAttendance.absentDate = attendance.absentDate
                    newAttendance.absentReason = attendance.absentReason

                    CoroutineScope(Dispatchers.IO).launch {
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
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

    override fun updateAbsentEntry(
        attendanceId: String,
        attendance: EmployeeAttendance,
    ): Resource<Boolean> {
        return if (user != null) {
            try {

                val employee =
                    realm.query<EmployeeRealm>("_id == $0", attendance.employee.employeeId).first()
                        .find()

                if (employee != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            val newAttendance =
                                this.query<AttendanceRealm>("_id == $0", attendanceId).first()
                                    .find()
                            newAttendance?.isAbsent = attendance.isAbsent
                            newAttendance?.absentDate = attendance.absentDate
                            newAttendance?.absentReason = attendance.absentReason
                            newAttendance?.updated_at = System.currentTimeMillis().toString()

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
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

    override fun removeAttendanceById(attendanceId: String): Resource<Boolean> {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    val attendance =
                        this.query<AttendanceRealm>("_id == $0", attendanceId).first().find()

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

    override fun removeAttendanceByEmployeeId(employeeId: String, date: String): Resource<Boolean> {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    val attendance =
                        this.query<AttendanceRealm>(
                            "employee._id == $0 AND absentDate == $1",
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