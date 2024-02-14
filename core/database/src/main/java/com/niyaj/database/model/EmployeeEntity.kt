package com.niyaj.database.model

import com.niyaj.model.Employee
import com.niyaj.model.EmployeeSalaryType
import com.niyaj.model.EmployeeType
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
class EmployeeEntity(): RealmObject {

    @PrimaryKey
    var employeeId: String = ""

    var employeeName: String = ""

    var employeePhone: String = ""

    var employeeSalary: String = ""

    var employeeSalaryType: String = EmployeeSalaryType.Daily.name

    var employeeType: String = EmployeeType.FullTime.name

    var employeePosition: String = ""

    var employeeJoinedDate: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null

    constructor(
        employeeId: String = "",
        employeeName: String = "",
        employeePhone: String = "",
        employeeSalary: String = "",
        employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Monthly,
        employeePosition: String = "",
        employeeType: EmployeeType = EmployeeType.FullTime,
        employeeJoinedDate: String = "",
        createdAt: String = System.currentTimeMillis().toString(),
        updatedAt: String? = null,
    ) : this() {
        this.employeeId = employeeId
        this.employeeName = employeeName
        this.employeePhone = employeePhone
        this.employeeSalary = employeeSalary
        this.employeeSalaryType = employeeSalaryType.name
        this.employeePosition = employeePosition
        this.employeeType = employeeType.name
        this.employeeJoinedDate = employeeJoinedDate
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun EmployeeEntity.toExternalModel(): Employee {
    return Employee(
        employeeId = this.employeeId,
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        employeeSalary = this.employeeSalary,
        employeeSalaryType = EmployeeSalaryType.valueOf(this.employeeSalaryType),
        employeeType = EmployeeType.valueOf(this.employeeType),
        employeePosition = this.employeePosition,
        employeeJoinedDate = this.employeeJoinedDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}