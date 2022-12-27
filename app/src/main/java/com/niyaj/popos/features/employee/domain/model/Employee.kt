package com.niyaj.popos.features.employee.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Employee(): RealmObject {

    @PrimaryKey
    var employeeId: String = ""

    var employeeName: String = ""

    var employeePhone: String = ""

    var employeeSalary: String = ""

    var employeeSalaryType: String = ""

    var employeeType: String = ""

    var employeePosition: String = ""

    var employeeJoinedDate: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        employeeId: String = "",
        employeeName: String = "",
        employeePhone: String = "",
        employeeSalary: String = "",
        employeeSalaryType: String = "",
        employeePosition: String = "",
        employeeType: String = "",
        employeeJoinedDate: String = "",
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null,
    ) : this() {
        this.employeeId = employeeId
        this.employeeName = employeeName
        this.employeePhone = employeePhone
        this.employeeSalary = employeeSalary
        this.employeeSalaryType = employeeSalaryType
        this.employeePosition = employeePosition
        this.employeeType = employeeType
        this.employeeJoinedDate = employeeJoinedDate
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}