package com.niyaj.popos.features.employee_salary.di

import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryValidationRepository
import com.niyaj.popos.features.employee_salary.domain.use_cases.AddNewSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.DeleteSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetAllSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetEmployeeSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetSalaryByEmployeeId
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetSalaryById
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetSalaryCalculableDate
import com.niyaj.popos.features.employee_salary.domain.use_cases.SalaryUseCases
import com.niyaj.popos.features.employee_salary.domain.use_cases.UpdateSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateEmployee
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateGiveDate
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidatePaymentType
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalary
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryNote
import com.niyaj.popos.features.employee_salary.domain.use_cases.validation.ValidateSalaryType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmployeeSalaryModule {

    @Provides
    @Singleton
    fun provideSalaryUseCases(salaryRepository: SalaryRepository, salaryValidationRepository: SalaryValidationRepository): SalaryUseCases {
        return SalaryUseCases(
            getAllSalary = GetAllSalary(salaryRepository),
            getSalaryById = GetSalaryById(salaryRepository),
            getSalaryByEmployeeId = GetSalaryByEmployeeId(salaryRepository),
            addNewSalary = AddNewSalary(salaryRepository),
            updateSalary = UpdateSalary(salaryRepository),
            deleteSalary = DeleteSalary(salaryRepository),
            getEmployeeSalary = GetEmployeeSalary(salaryRepository),
            getSalaryCalculableDate = GetSalaryCalculableDate(salaryRepository),
            validateGiveDate = ValidateGiveDate(salaryValidationRepository),
            validateEmployee = ValidateEmployee(salaryValidationRepository),
            validatePaymentType = ValidatePaymentType(salaryValidationRepository),
            validateSalary = ValidateSalary(salaryValidationRepository),
            validateSalaryNote = ValidateSalaryNote(salaryValidationRepository),
            validateSalaryType = ValidateSalaryType(salaryValidationRepository),
        )
    }
}