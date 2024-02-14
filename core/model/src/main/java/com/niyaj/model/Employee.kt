package com.niyaj.model

data class Employee(
    val employeeId: String = "",

    val employeeName: String = "",

    val employeePhone: String = "",

    val employeeSalary: String = "",

    val employeeSalaryType: EmployeeSalaryType = EmployeeSalaryType.Monthly,

    val employeeType: EmployeeType = EmployeeType.FullTime,

    val employeePosition: String = "",

    val employeeJoinedDate: String = "",

    val createdAt: String = System.currentTimeMillis().toString(),

    val updatedAt: String? = null,
)

/**
 * Filter employee by search text
 * @param searchText String
 * @return Boolean
 */
fun List<Employee>.filterEmployee(searchText: String): List<Employee> {
    return if (searchText.isNotEmpty()) {
        this.filter {
            it.employeeName.contains(searchText, true) ||
                    it.employeePosition.contains(searchText, true) ||
                    it.employeePhone.contains(searchText, true) ||
                    it.employeeSalaryType.name.contains(searchText, true) ||
                    it.employeeSalary.contains(searchText, true) ||
                    it.employeeJoinedDate.contains(searchText, true) ||
                    it.createdAt.contains(searchText, true) ||
                    it.updatedAt?.contains(searchText, true) == true
        }
    } else this
}