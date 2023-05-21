package com.niyaj.popos.features.employee_salary.di

import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetAllSalary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object EmployeeSalaryModule {

    /**
     * 
     */
    @Provides
    @Singleton
    fun provideSalaryUseCases(salaryRepository: SalaryRepository): GetAllSalary {
        return GetAllSalary(salaryRepository)
    }
}