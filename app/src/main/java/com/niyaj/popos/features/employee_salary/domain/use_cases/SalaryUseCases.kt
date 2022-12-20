package com.niyaj.popos.features.employee_salary.domain.use_cases

data class SalaryUseCases(
    val getAllSalary: GetAllSalary,
    val getSalaryById: GetSalaryById,
    val getSalaryByEmployeeId: GetSalaryByEmployeeId,
    val addNewSalary: AddNewSalary,
    val updateSalary: UpdateSalary,
    val deleteSalary: DeleteSalary,
    val getEmployeeSalary: GetEmployeeSalary,
    val getSalaryCalculableDate: GetSalaryCalculableDate,
)
