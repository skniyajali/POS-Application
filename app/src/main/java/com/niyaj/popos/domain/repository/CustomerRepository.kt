package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun getAllCustomers(): Flow<Resource<List<Customer>>>

    suspend fun getCustomerById(customerId: String): Resource<Customer?>

    fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean

    suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean>

    suspend fun deleteCustomer(customerId: String): Resource<Boolean>
}