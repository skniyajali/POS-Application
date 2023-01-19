package com.niyaj.popos.features.customer.data.repository

import android.util.Patterns
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.model.Contact
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
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
) : CustomerRepository, CustomerValidationRepository {

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
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(validateCustomerName, validateCustomerPhone, validateCustomerEmail).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher){
                    val customer = Customer()
                    customer.customerId = newCustomer.customerId.ifEmpty { BsonObjectId().toHexString() }
                    customer.customerName = newCustomer.customerName
                    customer.customerEmail = newCustomer.customerEmail
                    customer.customerPhone = newCustomer.customerPhone
                    customer.createdAt = newCustomer.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(customer)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to validate customer", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new customer", false)
        }
    }

    override suspend fun updateCustomer(newCustomer: Customer, customerId: String): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(validateCustomerName, validateCustomerPhone, validateCustomerEmail).any { !it.successful }

            if (!hasError) {
                val customer = realm.query<Customer>("customerId == $0", customerId).first().find()
                if (customer != null) {
                    withContext(ioDispatcher){
                        realm.write {
                            findLatest(customer)?.apply {
                                this.customerName = newCustomer.customerName
                                this.customerEmail = newCustomer.customerEmail
                                this.customerPhone = newCustomer.customerPhone
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find customer", false)
                }
            }else {
                Resource.Error("Unable to validate customer", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update customer", false)
        }
    }

    override suspend fun deleteCustomer(customerId: String): Resource<Boolean> {
        return try {
            val customer = realm.query<Customer>("customerId == $0", customerId).first().find()

            if (customer != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(customer)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error("Unable to find customer", false)
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

                    if (customers.isNotEmpty()) {
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
                        val customer = this.query<Customer>("customerPhone == $0", contact.phoneNo).first().find()

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

    override fun validateCustomerName(customerName: String?): ValidationResult {
        if(!customerName.isNullOrEmpty()) {
            if(customerName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer name must be 3 characters long",
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerEmail(customerEmail: String?): ValidationResult {
        if(!customerEmail.isNullOrEmpty()) {
            if(!Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer email is not a valid email address.",
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerPhone(customerPhone: String, customerId: String?): ValidationResult {
        if(customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not be empty",
            )
        }

        if(customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no must be 10 digits",
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if(containsLetters){
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no should not contains any characters"
            )
        }

        if(findCustomerByPhone(customerPhone, customerId)){
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no already exists"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}