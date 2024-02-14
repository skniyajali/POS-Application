package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import kotlinx.coroutines.flow.Flow

/**
 *  Interface for Customer to interact with data layer
 *  @see Customer
 */
interface CustomerRepository {

    /**
     * Get all customers from database.
     * @return [Flow] of [Resource] of [List] of [Customer] objects.
     * @see Customer
     */
    suspend fun getAllCustomers(searchText: String): Flow<List<Customer>>

    /**
     * Get customer by id from database.
     * @param customerId : id of customer
     * @return [Resource] of [Customer] object.
     * @see Customer
     */
    suspend fun getCustomerById(customerId: String): Resource<Customer?>

    /**
     * Get customer by phone from database.
     * @param customerPhone : phone of customer
     * @param customerId : id of customer
     * @return [Boolean] true if customer exists, false otherwise.
     * @see Customer
     */
    fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean

    /**
     * Create new customer in database.
     * @param newCustomer : [Customer] object to be created
     * @return [Resource] of [Boolean] true if customer created, false otherwise.
     * @see Customer
     */
    suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean>

    /**
     * Update customer in database.
     * @param newCustomer : [Customer] object to be updated
     * @param customerId : id of customer
     * @return [Resource] of [Boolean] true if customer updated, false otherwise.
     */
    suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean>

    /**
     * Delete customer from database.
     * @param customerId : id of customer to be deleted
     * @return [Resource] of [Boolean] true if customer deleted, false otherwise.
     * @see Customer
     */
    suspend fun deleteCustomer(customerId: String): Resource<Boolean>

    /**
     * Delete customer from database.
     * @param customerIds : list of id of customers to be deleted
     * @return [Resource] of [Boolean] true if customer deleted, false otherwise.
     * @see Customer
     */
    suspend fun deleteCustomers(customerIds: List<String>): Resource<Boolean>

    /**
     * Delete all customers from database.
     * @return [Resource] of [Boolean] true if all customers deleted, false otherwise.
     * @see Customer
     */
    suspend fun deleteAllCustomer(): Resource<Boolean>

    /**
     * Import customers from file.
     * @param customers : [List] of [Customer] objects to be imported
     * @return [Resource] of [Boolean] true if customers imported, false otherwise.
     */
    suspend fun importContacts(customers: List<Customer>): Resource<Boolean>

    suspend fun getCustomerWiseOrder(customerId : String): Flow<List<CustomerWiseOrder>>
}