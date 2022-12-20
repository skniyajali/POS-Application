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