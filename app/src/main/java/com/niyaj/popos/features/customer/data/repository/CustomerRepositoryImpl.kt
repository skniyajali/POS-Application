package com.niyaj.popos.features.customer.data.repository

import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CustomerRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CustomerRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Customer Session")
    }

    override suspend fun getAllCustomers(): Flow<Resource<List<Customer>>> {
        return channelFlow {
            withContext(ioDispatcher) {
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
    }

    override suspend fun getCustomerById(customerId: String): Resource<Customer?> {
        return try {
            val customer = withContext(ioDispatcher) {
                realm.query<Customer>(
                    "customerId == $0",
                    customerId
                ).first().find()
            }

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
            withContext(ioDispatcher){
                val customer = Customer()
                customer.customerId = BsonObjectId().toHexString()
                customer.customerName = newCustomer.customerName
                customer.customerEmail = newCustomer.customerEmail
                customer.customerPhone = newCustomer.customerPhone
                customer.createdAt = System.currentTimeMillis().toString()

                realm.write {
                    this.copyToRealm(customer)
                }
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
            withContext(ioDispatcher){
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

    override suspend fun deleteAllCustomer(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val customers = this.query<Customer>().find()

                    customers.forEach { customer ->
                        val cartOrder = this.query<CartOrder>("customer.customerId == $0", customer.customerId).find()
                        val cart = this.query<CartRealm>("cartOrder.customer.customerId == $0", customer.customerId).find()

                        if (cartOrder.isNotEmpty()){
                            delete(cartOrder)
                        }

                        if (cart.isNotEmpty()){
                            delete(cart)
                        }
                    }.also {
                        delete(customers)
                    }

                }
            }

            Resource.Success(true)
        } catch (e: Exception){
            Timber.e(e)

            Resource.Error(e.message?: "Unable to delete all customer", false)
        }
    }

    override suspend fun importContacts(contacts: List<Contact>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    contacts.forEach { contact ->
                        val customer = this.query<Customer>(
                            "customerPhone == $0",
                            contact.phoneNo
                        ).first().find()

                        if (customer == null){
                            val newCustomer = Customer()
                            newCustomer.customerId = BsonObjectId().toHexString()
                            newCustomer.customerName = contact.name
                            newCustomer.customerPhone = contact.phoneNo
                            newCustomer.customerEmail = contact.email
                            newCustomer.createdAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newCustomer)

                        }else {
                            customer.customerName = contact.name
                            customer.customerEmail = contact.email
                            customer.updatedAt = System.currentTimeMillis().toString()
                        }
                    }
                }
            }

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import contacts", false)
        }

    }
}