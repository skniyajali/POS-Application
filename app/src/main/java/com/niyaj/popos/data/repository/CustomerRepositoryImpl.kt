package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.customer.CustomerRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CustomerRepositoryImpl(
    private val customerRealmDao: CustomerRealmDao
) : CustomerRepository {

    override suspend fun getAllCustomers(): Flow<Resource<List<Customer>>> {
        return flow {
            customerRealmDao.getAllCustomer().collect { result ->
                when(result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.let { categories ->
                                categories.map { customer ->
                                    Customer(
                                        customerId = customer._id,
                                        customerPhone = customer.customerPhone,
                                        customerName = customer.customerName,
                                        customerEmail = customer.customerEmail,
                                        created_at = customer.created_at,
                                        updated_at = customer.updated_at
                                    )
                                }
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get customers from the database"))
                    }
                }
            }
        }
    }

    override suspend fun getCustomerById(customerId: String): Resource<Customer?> {
        val result = customerRealmDao.getCustomerById(customerId)

        return result.data?.let { customer ->
            Resource.Success(
                Customer(
                    customerId = customer._id,
                    customerPhone = customer.customerPhone,
                    customerName = customer.customerName,
                    customerEmail = customer.customerEmail,
                    created_at = customer.created_at,
                    updated_at = customer.updated_at
                )
            )
        } ?: Resource.Error(result.message ?: "Unable to get customers from the database")
    }

    override fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean {
        return customerRealmDao.findCustomerByPhone(customerPhone, customerId)
    }

    override suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean> {
        return customerRealmDao.createNewCustomer(newCustomer)
    }

    override suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean> {
        return customerRealmDao.updateCustomer(newCustomer, customerId)
    }

    override suspend fun deleteCustomer(customerId: String): Resource<Boolean> {
        return customerRealmDao.deleteCustomer(customerId)
    }
}