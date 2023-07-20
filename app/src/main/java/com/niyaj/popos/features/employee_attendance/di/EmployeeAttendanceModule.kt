package com.niyaj.popos.features.employee_attendance.di

import com.niyaj.popos.features.employee_attendance.data.repository.AttendanceRepositoryImpl
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetAllAttendance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmployeeAttendanceModule {

    @Provides
    fun provideAttendanceRepositoryImpl(config : RealmConfiguration) : AttendanceRepository {
        return AttendanceRepositoryImpl(config)
    }

    @Provides
    fun provideAttendanceValidationRepositoryImpl(config : RealmConfiguration) : AttendanceValidationRepository {
        return AttendanceRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideGetAllAttendanceUseCases(attendanceRepository : AttendanceRepository) : GetAllAttendance {
        return GetAllAttendance(attendanceRepository)
    }
}