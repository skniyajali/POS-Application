package com.niyaj.popos.features.employee.di

import com.niyaj.popos.features.employee.domain.repository.EmployeeRepository
import com.niyaj.popos.features.employee.domain.use_cases.GetAllEmployee
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module that provides the [GetAllEmployee] use case.
 * @see [GetAllEmployee]
 * @see [EmployeeRepository]
 */
@Module
@InstallIn(SingletonComponent::class)
object EmployeeModule {

    /**
     * Provides the [GetAllEmployee] use case.
     * @param employeeRepository The [EmployeeRepository] to be used by the use case.
     * @return The [GetAllEmployee] use case.
     */
    @Provides
    @Singleton
    fun provideGetAllEmployeeUseCases(employeeRepository: EmployeeRepository): GetAllEmployee {
        return GetAllEmployee(employeeRepository)
    }
}