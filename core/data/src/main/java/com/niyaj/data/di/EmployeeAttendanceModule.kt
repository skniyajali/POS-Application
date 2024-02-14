package com.niyaj.data.di

import com.niyaj.common.network.Dispatcher
import com.niyaj.common.network.PoposDispatchers
import com.niyaj.data.data.repository.AttendanceRepositoryImpl
import com.niyaj.data.repository.AttendanceRepository
import com.niyaj.data.repository.validation.AttendanceValidationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object EmployeeAttendanceModule {

    @Provides
    fun provideAttendanceRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): AttendanceRepository {
        return AttendanceRepositoryImpl(config, ioDispatcher)
    }

    @Provides
    fun provideAttendanceValidationRepositoryImpl(
        config: RealmConfiguration,
        @Dispatcher(PoposDispatchers.IO)
        ioDispatcher: CoroutineDispatcher
    ): AttendanceValidationRepository {
        return AttendanceRepositoryImpl(config, ioDispatcher)
    }
}