package com.niyaj.popos.features.employee_salary.domain.use_cases

import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateEmployee
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateGiveDate
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidatePaymentType
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryNote
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryType

data class SalaryUseCases(
    val validateGiveDate: ValidateGiveDate,
    val validateEmployee: ValidateEmployee,
    val validatePaymentType: ValidatePaymentType,
    val validateSalary: ValidateSalary,
    val validateSalaryNote: ValidateSalaryNote,
    val validateSalaryType: ValidateSalaryType,
    val getAllSalary: GetAllSalary,
    val getSalaryById: GetSalaryById,
    val getSalaryByEmployeeId: GetSalaryByEmployeeId,
    val addNewSalary: AddNewSalary,
    val updateSalary: UpdateSalary,
    val deleteSalary: DeleteSalary,
    val getEmployeeSalary: GetEmployeeSalary,
    val getSalaryCalculableDate: GetSalaryCalculableDate,
)
