package com.niyaj.popos.features.customer.di

import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.use_cases.GetAllCustomers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for providing customer related dependencies
 * @see CustomerRepository
 * @see GetAllCustomers
 */
@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {
    /**
     * Providing methods for getting customers from the repository
     * @param customerRepository the repository to get the customers from the database
     * @return the use case to get all customers
     * @see CustomerRepository.getAllCustomers
     */
    @Provides
    @Singleton
    fun provideGetAllCustomerCases(customerRepository: CustomerRepository): GetAllCustomers {
        return GetAllCustomers(customerRepository)
    }
}