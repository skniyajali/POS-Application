package com.niyaj.data.mapper

import com.niyaj.database.model.AttendanceEntity
import com.niyaj.model.Attendance

fun Attendance.toEntity(): AttendanceEntity {
    return AttendanceEntity(
        attendeeId = this.attendeeId,
        employee = this.employee?.toEntity(),
        isAbsent = this.isAbsent,
        absentReason = this.absentReason,
        absentDate = this.absentDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}