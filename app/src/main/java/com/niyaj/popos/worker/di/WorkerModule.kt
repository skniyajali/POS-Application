package com.niyaj.popos.worker.di

import com.niyaj.popos.worker.status.MonitorWorkManager
import com.niyaj.popos.worker.status.WorkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface WorkerModule {
    @Binds
    fun bindsWorkMonitor(
        monitorWorkManager: MonitorWorkManager
    ): WorkMonitor
}