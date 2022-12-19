package com.niyaj.popos.realm.customer.di

import com.niyaj.popos.realm.customer.domain.repository.CustomerRepository
import com.niyaj.popos.realm.customer.domain.use_cases.CreateNewCustomer
import com.niyaj.popos.realm.customer.domain.use_cases.CustomerUseCases
import com.niyaj.popos.realm.customer.domain.use_cases.DeleteCustomer
import com.niyaj.popos.realm.customer.domain.use_cases.FindCustomerByPhone
import com.niyaj.popos.realm.customer.domain.use_cases.GetAllCustomers
import com.niyaj.popos.realm.customer.domain.use_cases.GetCustomerById
import com.niyaj.popos.realm.customer.domain.use_cases.UpdateCustomer
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
    fun provideCustomerCases(customerRepository: CustomerRepository): CustomerUseCases {
        return CustomerUseCases(
            getAllCustomers = GetAllCustomers(customerRepository),
            getCustomerById = GetCustomerById(customerRepository),
            findCustomerByPhone = FindCustomerByPhone(customerRepository),
            createNewCustomer = CreateNewCustomer(customerRepository),
            updateCustomer = UpdateCustomer(customerRepository),
            deleteCustomer = DeleteCustomer(customerRepository),
        )
    }
}