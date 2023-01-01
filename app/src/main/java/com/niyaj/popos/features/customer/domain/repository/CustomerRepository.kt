package com.niyaj.popos.features.customer.domain.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.domain.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {

    suspend fun getAllCustomers(): Flow<Resource<List<Customer>>>

    suspend fun getCustomerById(customerId: String): Resource<Customer?>

    fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean

    suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean>

    suspend fun deleteCustomer(customerId: String): Resource<Boolean>

    suspend fun deleteAllCustomer(): Resource<Boolean>

    suspend fun importContacts(contacts: List<Contact>): Resource<Boolean>
}