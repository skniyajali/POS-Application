package com.niyaj.popos.features.employee.di

import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee.domain.use_cases.CreateNewEmployee
import com.niyaj.popos.features.employee.domain.use_cases.DeleteEmployee
import com.niyaj.popos.features.employee.domain.use_cases.EmployeeUseCases
import com.niyaj.popos.features.employee.domain.use_cases.FindEmployeeByName
import com.niyaj.popos.features.employee.domain.use_cases.FindEmployeeByPhone
import com.niyaj.popos.features.employee.domain.use_cases.GetAllEmployee
import com.niyaj.popos.features.employee.domain.use_cases.GetEmployeeById
import com.niyaj.popos.features.employee.domain.use_cases.UpdateEmployee
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
    fun provideEmployeeUseCases(employeeRepository: EmployeeRepository): EmployeeUseCases {
        return EmployeeUseCases(
            getAllEmployee = GetAllEmployee(employeeRepository),
            getEmployeeById = GetEmployeeById(employeeRepository),
            findEmployeeByName = FindEmployeeByName(employeeRepository),
            findEmployeeByPhone = FindEmployeeByPhone(employeeRepository),
            createNewEmployee = CreateNewEmployee(employeeRepository),
            updateEmployee = UpdateEmployee(employeeRepository),
            deleteEmployee = DeleteEmployee(employeeRepository),
        )
    }
}