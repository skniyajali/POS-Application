package com.niyaj.popos.features.employee_attendance.domain.model

import com.niyaj.popos.common.utils.toFormattedDate
import com.niyaj.popos.features.employee.domain.model.Employee
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Employee Attendance model
 *
 */
class EmployeeAttendance(): RealmObject {

    @PrimaryKey
    var attendeeId: String = ""

    var employee: Employee? = null

    var isAbsent: Boolean = true

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

/**
 *
 */
fun EmployeeAttendance.filterEmployeeAttendance(searchText: String): Boolean {
    return this.absentDate.toFormattedDate.contains(searchText, true) ||
            this.absentReason.contains(searchText, true) ||
            this.isAbsent.toString().contains(searchText, true) ||
            this.employee?.employeeName?.contains(searchText, true) == true ||
            this.employee?.employeeSalary?.contains(searchText, true) == true ||
            this.employee?.employeePhone?.contains(searchText, true) == true ||
            this.employee?.employeePosition?.contains(searchText, true) == true ||
            this.employee?.employeeJoinedDate?.contains(searchText, true) == true ||
            this.employee?.employeeSalaryType?.contains(searchText, true) == true
}