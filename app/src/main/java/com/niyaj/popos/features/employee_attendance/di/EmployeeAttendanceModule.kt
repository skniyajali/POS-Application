package com.niyaj.popos.features.employee_attendance.di

import com.niyaj.popos.features.employee_attendance.domain.repository.AttendanceRepository
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AddAbsentEntry
import com.niyaj.popos.features.employee_attendance.domain.use_cases.AttendanceUseCases
import com.niyaj.popos.features.employee_attendance.domain.use_cases.DeleteAttendanceByEmployeeId
import com.niyaj.popos.features.employee_attendance.domain.use_cases.DeleteAttendanceById
import com.niyaj.popos.features.employee_attendance.domain.use_cases.FindAttendanceByAbsentDate
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetAllAttendance
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetAttendanceById
import com.niyaj.popos.features.employee_attendance.domain.use_cases.GetMonthlyAbsentReports
import com.niyaj.popos.features.employee_attendance.domain.use_cases.UpdateAbsentEntry
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
    fun provideAttendanceUseCases(attendanceRepository: AttendanceRepository): AttendanceUseCases {
        return AttendanceUseCases(
            getAllAttendance = GetAllAttendance(attendanceRepository),
            getAttendanceById = GetAttendanceById(attendanceRepository),
            findAttendanceByAbsentDate = FindAttendanceByAbsentDate(attendanceRepository),
            addAbsentEntry = AddAbsentEntry(attendanceRepository),
            updateAbsentEntry = UpdateAbsentEntry(attendanceRepository),
            deleteAttendanceById = DeleteAttendanceById(attendanceRepository),
            deleteAttendanceByEmployeeId = DeleteAttendanceByEmployeeId(attendanceRepository),
            getMonthlyAbsentReports = GetMonthlyAbsentReports(attendanceRepository),

            )
    }
}