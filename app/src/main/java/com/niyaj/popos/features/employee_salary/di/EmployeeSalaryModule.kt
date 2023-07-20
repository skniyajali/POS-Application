package com.niyaj.popos.features.employee_salary.di

import com.niyaj.popos.features.employee_salary.data.repository.SalaryRepositoryImpl
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryRepository
import com.niyaj.popos.features.employee_salary.domain.repository.SalaryValidationRepository
import com.niyaj.popos.features.employee_salary.domain.use_cases.GetAllSalary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

/**
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object EmployeeSalaryModule {

    @Provides
    fun provideSalaryRepositoryImpl(config : RealmConfiguration) : SalaryRepository {
        return SalaryRepositoryImpl(config)
    }

    @Provides
    fun provideSalaryValidationRepositoryImpl(config : RealmConfiguration) : SalaryValidationRepository {
        return SalaryRepositoryImpl(config)
    }

    /**
     *
     */
    @Provides
    @Singleton
    fun provideSalaryUseCases(salaryRepository : SalaryRepository) : GetAllSalary {
        return GetAllSalary(salaryRepository)
    }
}