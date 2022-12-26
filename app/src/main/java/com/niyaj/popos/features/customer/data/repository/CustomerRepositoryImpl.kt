package com.niyaj.popos.features.customer.data.repository

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CustomerRepositoryImpl(
    config: RealmConfiguration
) : CustomerRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Customer Session")
    }

    override suspend fun getAllCustomers(): Flow<Resource<List<Customer>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))
                val items = realm.query<Customer>()
                    .sort("customerId", Sort.DESCENDING).find()
                val itemsFlow = items.asFlow()
                itemsFlow.collect { changes: ResultsChange<Customer> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }

                        else -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }
                    }
                }
            } catch (e: Exception) {
                send(Resource.Loading(false))
                send(Resource.Error(e.message ?: "Unable to get customers", emptyList()))
            }
        }
    }

    override suspend fun getCustomerById(customerId: String): Resource<Customer?> {
        return try {
            val customer = realm.query<Customer>(
                "customerId == $0",
                customerId
            ).first().find()
            Resource.Success(customer)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get customers", null)
        }
    }

    override fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean {
        val customer = if (customerId == null) {
            realm.query<Customer>(
                "customerPhone == $0",
                customerPhone
            ).first().find()
        } else {
            realm.query<Customer>(
                "customerId != $0 && customerPhone == $1",
                customerId,
                customerPhone
            ).first().find()
        }

        return customer != null
    }

    override suspend fun createNewCustomer(newCustomer: Customer): Resource<Boolean> {
        return try {
            val customer = Customer()
            customer.customerId = BsonObjectId().toHexString()
            customer.customerName = newCustomer.customerName
            customer.customerEmail = newCustomer.customerEmail
            customer.customerPhone = newCustomer.customerPhone
            customer.createdAt = System.currentTimeMillis().toString()

            realm.write {
                this.copyToRealm(customer)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new customer", false)
        }
    }

    override suspend fun updateCustomer(
        newCustomer: Customer,
        customerId: String
    ): Resource<Boolean> {
        return try {
            realm.write {
                val customer = this.query<Customer>(
                    "customerId == $0",
                    customerId
                ).first().find()
                customer?.customerName = newCustomer.customerName
                customer?.customerEmail = newCustomer.customerEmail
                customer?.customerPhone = newCustomer.customerPhone
                customer?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update customer", false)
        }
    }

    override suspend fun deleteCustomer(customerId: String): Resource<Boolean> {
        return try {
            realm.write {
                val customer = this.query<Customer>(
                    "customerId == $0",
                    customerId
                ).first().find()

                if (customer != null) {
                    delete(customer)
                    Resource.Success(true)

                } else {
                    Resource.Error("Unable to find customer", false)
                }
            }

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customer", false)
        }
    }
}