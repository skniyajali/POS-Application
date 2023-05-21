package com.niyaj.popos.features.reports.di

import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.reports.domain.repository.ReportsRepository
import com.niyaj.popos.features.reports.domain.use_cases.GetAddressWiseReport
import com.niyaj.popos.features.reports.domain.use_cases.GetCustomerWiseReport
import com.niyaj.popos.features.reports.domain.use_cases.GetProductWiseReport
import com.niyaj.popos.features.reports.domain.use_cases.GetReportsBarData
import com.niyaj.popos.features.reports.domain.use_cases.ReportsUseCases
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
    fun provideReportsUseCases(reportsRepository: ReportsRepository, addressRepository: AddressRepository, customerRepository: CustomerRepository): ReportsUseCases {
        return ReportsUseCases(
            getReportsBarData = GetReportsBarData(reportsRepository),
            getProductWiseReport = GetProductWiseReport(reportsRepository),
            getAddressWiseReport = GetAddressWiseReport(reportsRepository, addressRepository),
            getCustomerWiseReport = GetCustomerWiseReport(reportsRepository, customerRepository),
        )
    }
}