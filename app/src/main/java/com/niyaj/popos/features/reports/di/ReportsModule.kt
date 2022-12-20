package com.niyaj.popos.features.reports.di

import com.niyaj.popos.features.reports.domain.reports.DeletePastData
import com.niyaj.popos.features.reports.domain.reports.GenerateReport
import com.niyaj.popos.features.reports.domain.reports.GetProductWiseReport
import com.niyaj.popos.features.reports.domain.reports.GetReport
import com.niyaj.popos.features.reports.domain.reports.GetReportsBarData
import com.niyaj.popos.features.reports.domain.reports.ReportsUseCases
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportsModule {

    @Provides
    @Singleton
    fun provideReportsUseCases(reportsRepository: ReportsRepository): ReportsUseCases {
        return ReportsUseCases(
            generateReport = GenerateReport(reportsRepository),
            getReport = GetReport(reportsRepository),
            getReportsBarData = GetReportsBarData(reportsRepository),
            getProductWiseReport = GetProductWiseReport(reportsRepository),
            deletePastData = DeletePastData(reportsRepository),
        )
    }
}