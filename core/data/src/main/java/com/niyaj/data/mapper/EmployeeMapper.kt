package com.niyaj.data.mapper

import com.niyaj.database.model.EmployeeEntity
import com.niyaj.model.Employee
import org.mongodb.kbson.BsonObjectId

fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        employeeId = this.employeeId.ifEmpty { BsonObjectId().toHexString() },
        employeeName = this.employeeName,
        employeePhone = this.employeePhone,
        employeeSalary = this.employeeSalary,
        employeeSalaryType = this.employeeSalaryType,
        employeeType = this.employeeType,
        employeePosition = this.employeePosition,
        employeeJoinedDate = this.employeeJoinedDate,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}