package com.niyaj.model

import com.niyaj.common.utils.toFormattedDate

data class Attendance(
    val attendeeId: String = "",

    val employee: Employee? = null,

    val isAbsent: Boolean = true,

    val absentReason: String = "",

    val absentDate: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)


/**
 *
 */
fun List<Attendance>.filterEmployeeAttendance(searchText: String): List<Attendance> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.absentDate.toFormattedDate.contains(searchText, true) ||
                    it.absentReason.contains(searchText, true) ||
                    it.isAbsent.toString().contains(searchText, true) ||
                    it.employee?.employeeName?.contains(searchText, true) == true ||
                    it.employee?.employeeSalary?.contains(searchText, true) == true ||
                    it.employee?.employeePhone?.contains(searchText, true) == true ||
                    it.employee?.employeePosition?.contains(searchText, true) == true ||
                    it.employee?.employeeJoinedDate?.contains(searchText, true) == true ||
                    it.employee?.employeeSalaryType?.name?.contains(searchText, true) == true
        }
    } else this
}