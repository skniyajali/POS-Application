package com.niyaj.popos.features.employee.di

import com.niyaj.popos.features.employee.data.repository.EmployeeValidationRepository
import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee.domain.use_cases.CreateNewEmployee
import com.niyaj.popos.features.employee.domain.use_cases.DeleteEmployee
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.domain.use_cases.GetAllEmployee
import com.niyaj.popos.features.employee.domain.use_cases.GetEmployeeById
import com.niyaj.popos.features.employee.domain.use_cases.UpdateEmployee
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeName
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePhone
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeePosition
import com.niyaj.popos.features.employee.domain.use_cases.validation.ValidateEmployeeSalary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmployeeModule {

    @Provides
    @Singleton
    fun provideEmployeeUseCases(employeeRepository: EmployeeRepository, employeeValidationRepository: EmployeeValidationRepository): EmployeeUseCases {
        return EmployeeUseCases(
            getAllEmployee = GetAllEmployee(employeeRepository),
            getEmployeeById = GetEmployeeById(employeeRepository),
            createNewEmployee = CreateNewEmployee(employeeRepository),
            updateEmployee = UpdateEmployee(employeeRepository),
            deleteEmployee = DeleteEmployee(employeeRepository),
            validateEmployeeName = ValidateEmployeeName(employeeValidationRepository),
            validateEmployeePhone = ValidateEmployeePhone(employeeValidationRepository),
            validateEmployeePosition = ValidateEmployeePosition(employeeValidationRepository),
            validateEmployeeSalary = ValidateEmployeeSalary(employeeValidationRepository),
        )
    }
}