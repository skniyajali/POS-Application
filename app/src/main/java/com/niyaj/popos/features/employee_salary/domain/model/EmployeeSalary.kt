package com.niyaj.popos.features.employee_salary.domain.model

import com.niyaj.popos.features.employee.domain.model.Employee
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class EmployeeSalary(): RealmObject {
    @PrimaryKey
    var salaryId: String = ""

    var employee: Employee? = null

    var salaryType: String = ""

    var employeeSalary: String = ""

    var salaryGivenDate: String = ""

    var salaryPaymentType: String = ""

    var salaryNote: String = ""

    var createdAt: String = ""

    var updatedAt: String? = null


    constructor(
        salaryId: String = "",
        employee: Employee? = null,
        salaryType: String = "",
        employeeSalary: String = "",
        salaryGivenDate: String = "",
        salaryPaymentType: String = "",
        salaryNote: String = "",
        createdAt: String = "",
        updatedAt: String? = null
    ): this() {
        this.salaryId = salaryId
        this.employee = employee
        this.salaryType = salaryType
        this.employeeSalary = employeeSalary
        this.salaryGivenDate = salaryGivenDate
        this.salaryPaymentType = salaryPaymentType
        this.salaryNote = salaryNote
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}

fun EmployeeSalary.filterEmployeeSalary(searchText: String): Boolean {
    return this.employeeSalary.contains(searchText, true) ||
            this.salaryType.contains(searchText, true) ||
            this.salaryGivenDate.contains(searchText, true) ||
            this.salaryPaymentType.contains(searchText, true) ||
            this.salaryNote.contains(searchText, true) ||
            this.createdAt.contains(searchText, true) ||
            this.updatedAt?.contains(searchText, true) == true ||
            this.employee?.employeeName?.contains(searchText, true) == true ||
            this.employee?.employeePhone?.contains(searchText, true) == true ||
            this.employee?.employeeType?.contains(searchText, true) == true ||
            this.employee?.employeePosition?.contains(searchText, true) == true
}