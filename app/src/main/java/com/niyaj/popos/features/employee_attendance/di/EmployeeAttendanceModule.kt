package com.niyaj.popos.features.employee_attendance.di

import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceValidationRepository
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AddAbsentEntry
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.features.employee_attendance.domain.use_cases.DeleteAttendanceByEmployeeId
import com.niyaj.popos.features.employee_attendance.domain.use_cases.DeleteAttendanceById
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetAllAttendance
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetAttendanceById
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetMonthlyAbsentReports
import com.niyaj.popos.features.employee_attendance.domain.use_cases.UpdateAbsentEntry
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentDate
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateAbsentEmployee
import com.niyaj.popos.features.employee_attendance.domain.use_cases.validation.ValidateIsAbsent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EmployeeAttendanceModule {


    @Provides
    @Singleton
    fun provideAttendanceUseCases(attendanceRepository: AttendanceRepository, attendanceValidationRepository: AttendanceValidationRepository): AttendanceUseCases {
        return AttendanceUseCases(
            getAllAttendance = GetAllAttendance(attendanceRepository),
            getAttendanceById = GetAttendanceById(attendanceRepository),
            addAbsentEntry = AddAbsentEntry(attendanceRepository),
            updateAbsentEntry = UpdateAbsentEntry(attendanceRepository),
            deleteAttendanceById = DeleteAttendanceById(attendanceRepository),
            deleteAttendanceByEmployeeId = DeleteAttendanceByEmployeeId(attendanceRepository),
            getMonthlyAbsentReports = GetMonthlyAbsentReports(attendanceRepository),
            validateAbsentDate = ValidateAbsentDate(attendanceValidationRepository),
            validateAbsentEmployee = ValidateAbsentEmployee(attendanceValidationRepository),
            validateIsAbsent = ValidateIsAbsent(attendanceValidationRepository),
        )
    }
}