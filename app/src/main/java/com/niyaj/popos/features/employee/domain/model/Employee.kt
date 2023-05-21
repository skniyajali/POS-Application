package com.niyaj.popos.features.employee.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Employee Realm Object.
 *
 * @property employeeId [String]
 * @property employeeName [String]
 * @property employeePhone [String]
 * @property employeeSalary [String]
 * @property employeeSalaryType [String]
 * @property employeeType [String]
 * @property employeePosition [String]
 * @property employeeJoinedDate [String]
 * @property createdAt [String]
 * @property updatedAt [String]
 */
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

/**
 * Filter employee by search text
 * @param searchText String
 * @return Boolean
 */
fun Employee.filterEmployee(searchText: String) : Boolean {
    return this.employeeName.contains(searchText, true) ||
            this.employeePosition.contains(searchText, true) ||
            this.employeePhone.contains(searchText, true) ||
            this.employeeSalaryType.contains(searchText, true) ||
            this.employeeSalary.contains(searchText, true) ||
            this.employeeJoinedDate.contains(searchText, true) ||
            this.createdAt.contains(searchText, true) ||
            this.updatedAt?.contains(searchText, true) == true
}