package com.niyaj.popos.features.customer.di

import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import com.niyaj.popos.features.customer.domain.use_cases.CreateNewCustomer
import com.niyaj.popos.features.customer.domain.use_cases.CustomerUseCases
import com.niyaj.popos.features.customer.domain.use_cases.DeleteAllCustomers
import com.niyaj.popos.features.customer.domain.use_cases.DeleteCustomer
import com.niyaj.popos.features.customer.domain.use_cases.GetAllCustomers
import com.niyaj.popos.features.customer.domain.use_cases.GetCustomerById
import com.niyaj.popos.features.customer.domain.use_cases.ImportContacts
import com.niyaj.popos.features.customer.domain.use_cases.UpdateCustomer
import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerEmail
import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerName
import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerPhone
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CustomerModule {
    @Provides
    @Singleton
    fun provideCustomerCases(customerRepository: CustomerRepository, customerValidationRepository: CustomerValidationRepository): CustomerUseCases {
        return CustomerUseCases(
            getAllCustomers = GetAllCustomers(customerRepository),
            getCustomerById = GetCustomerById(customerRepository),
            createNewCustomer = CreateNewCustomer(customerRepository),
            updateCustomer = UpdateCustomer(customerRepository),
            deleteCustomer = DeleteCustomer(customerRepository),
            deleteAllCustomers = DeleteAllCustomers(customerRepository),
            importContacts = ImportContacts(customerRepository),
            validateCustomerName = ValidateCustomerName(customerValidationRepository),
            validateCustomerEmail = ValidateCustomerEmail(customerValidationRepository),
            validateCustomerPhone = ValidateCustomerPhone(customerValidationRepository),
        )
    }
}