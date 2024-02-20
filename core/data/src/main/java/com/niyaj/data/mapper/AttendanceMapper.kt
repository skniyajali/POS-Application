package com.niyaj.data.mapper

import com.niyaj.database.model.AttendanceEntity
import com.niyaj.database.model.EmployeeEntity
import com.niyaj.model.Attendance
import org.mongodb.kbson.BsonObjectId

fun Attendance.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        attendeeId = this.attendeeId.ifEmpty { BsonObjectId().toHexString() },
        employee = this.employee?.toEntity(),
        isAbsent = this.isAbsent,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}


fun Attendance.toEntity(employeeEntity: EmployeeEntity?): AttendanceEntity {
    return AttendanceEntity(
        attendeeId = this.attendeeId.ifEmpty { BsonObjectId().toHexString() },
        employee = employeeEntity,
        isAbsent = this.isAbsent,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}