package com.niyaj.popos.realm.employee_attendance.domain.model

import com.niyaj.popos.realm.employee.domain.model.Employee
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class EmployeeAttendance(): RealmObject {

    @PrimaryKey
    var attendeeId: String = ""

    var employee: Employee? = null

    var isAbsent: Boolean = false

    var absentReason: String = ""

    var absentDate: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        attendeeId: String = "",
        employee: Employee? = null,
        isAbsent: Boolean = false,
        absentReason: String = "",
        absentDate: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.attendeeId = attendeeId
        this.employee = employee
        this.isAbsent = isAbsent
        this.absentReason = absentReason
        this.absentDate = absentDate
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

}