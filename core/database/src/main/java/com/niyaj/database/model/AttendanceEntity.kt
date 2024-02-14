package com.niyaj.database.model

import com.niyaj.model.Attendance
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Employee Attendance model
 *
 */
class AttendanceEntity() : RealmObject {

    @PrimaryKey
    var attendeeId: String = ""

    var employee: EmployeeEntity? = null

    var isAbsent: Boolean = true

    var absentReason: String = ""

    var absentDate: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        attendeeId: String = "",
        employee: EmployeeEntity? = null,
        isAbsent: Boolean = false,
        absentReason: String = "",
        absentDate: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ) : this() {
        this.attendeeId = attendeeId
        this.employee = employee
        this.isAbsent = isAbsent
        this.absentReason = absentReason
        this.absentDate = absentDate
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}


fun AttendanceEntity.toExternalModel(): Attendance {
    return Attendance(
        attendeeId = this.attendeeId,
        employee = this.employee?.toExternalModel(),
        isAbsent = this.isAbsent,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}