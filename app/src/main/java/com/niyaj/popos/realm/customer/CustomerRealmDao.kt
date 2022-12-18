package com.niyaj.popos.realm.customer

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CustomerRealmDao {

    suspend fun getAllCustomer(): Flow<Resource<List<CustomerRealm>>>

    suspend fun getCustomerById(customerId: String): Resource<CustomerRealm?>

    fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean

    suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean>

    suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean>

    suspend fun deleteCustomer(customerId: String): Resource<Boolean>
}