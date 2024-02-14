package com.niyaj.data.data.repository

import android.util.Patterns
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_EMAIL_VALID_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CustomerTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.repository.CustomerRepository
import com.niyaj.data.repository.validation.CustomerValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.ChargesEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Customer
import com.niyaj.model.CustomerWiseOrder
import com.niyaj.model.OrderType
import com.niyaj.model.filterCustomer
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

/**
 * Created by Niyaj on 5/26/2020.
 * @author Niyaj
 * @param config : Realm Configuration
 * @param ioDispatcher : Coroutine Dispatcher
 * @see CustomerRepository
 * @see CustomerValidationRepository
 */
class CustomerRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher
) : CustomerRepository, CustomerValidationRepository {

    /**
     * Realm instance to interact with database.
     * @see Realm
     * @see RealmConfiguration
     */
    val realm: Realm = Realm.open(config)

    init {
        Timber.d("Customer Session")
    }

    override suspend fun getAllCustomers(searchText: String): Flow<List<Customer>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<CustomerEntity>()
                        .sort("customerId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterCustomer(searchText) },
                        send = { send(it) }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getCustomerById(customerId: String): Resource<Customer?> {
        return try {
            val customer =
                realm.query<CustomerEntity>("customerId == $0", customerId).first().find()

            Resource.Success(customer?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get customers")
        }
    }

    override fun findCustomerByPhone(customerPhone: String, customerId: String?): Boolean {
        val customer = if (customerId == null) {
            realm.query<CustomerEntity>(
                "customerPhone == $0",
                customerPhone
            ).first().find()
        } else {
            realm.query<CustomerEntity>(
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

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val customer = CustomerEntity()
                    customer.customerId =
                        newCustomer.customerId.ifEmpty { BsonObjectId().toHexString() }
                    customer.customerName = newCustomer.customerName
                    customer.customerEmail = newCustomer.customerEmail
                    customer.customerPhone = newCustomer.customerPhone
                    customer.createdAt =
                        newCustomer.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(customer)
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create new customer")
        }
    }

    override suspend fun updateCustomer(
        newCustomer: Customer,
        customerId: String
    ): Resource<Boolean> {
        return try {
            val validateCustomerName = validateCustomerName(newCustomer.customerName)
            val validateCustomerPhone = validateCustomerPhone(newCustomer.customerPhone)
            val validateCustomerEmail = validateCustomerEmail(newCustomer.customerEmail)

            val hasError = listOf(
                validateCustomerName,
                validateCustomerPhone,
                validateCustomerEmail
            ).any { !it.successful }

            if (!hasError) {
                val customer =
                    realm.query<CustomerEntity>("customerId == $0", customerId).first().find()
                if (customer != null) {
                    withContext(ioDispatcher) {
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
                } else {
                    Resource.Error("Unable to find customer")
                }
            } else {
                Resource.Error("Unable to validate customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update customer")
        }
    }

    override suspend fun deleteCustomer(customerId: String): Resource<Boolean> {
        return try {
            val customer =
                realm.query<CustomerEntity>("customerId == $0", customerId).first().find()

            if (customer != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        val cartOrder =
                            this.query<CartOrderEntity>("customer.customerId == $0", customerId)
                                .find()
                        val cart =
                            this.query<CartEntity>(
                                "cartOrder.customer.customerId == $0",
                                customerId
                            )
                                .find()

                        if (cartOrder.isNotEmpty()) {
                            delete(cartOrder)
                        }

                        if (cart.isNotEmpty()) {
                            delete(cart)
                        }

                        findLatest(customer)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find customer")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customer")
        }
    }

    override suspend fun deleteCustomers(customerIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                customerIds.forEach { customerId ->
                    val customer = realm
                        .query<CustomerEntity>("customerId == $0", customerId)
                        .first()
                        .find()

                    if (customer != null) {
                        withContext(ioDispatcher) {
                            realm.write {
                                val cartOrder = this.query<CartOrderEntity>(
                                    "customer.customerId == $0",
                                    customerId
                                ).find()

                                val cart = this.query<CartEntity>(
                                    "cartOrder.customer.customerId == $0",
                                    customerId
                                ).find()

                                if (cartOrder.isNotEmpty()) {
                                    delete(cartOrder)
                                }

                                if (cart.isNotEmpty()) {
                                    delete(cart)
                                }

                                findLatest(customer)?.let {
                                    delete(it)
                                }
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete customer")
        }
    }

    override suspend fun deleteAllCustomer(): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    val customers = this.query<CustomerEntity>().find()

                    if (customers.isNotEmpty()) {
                        customers.forEach { customer ->
                            val cartOrder = this.query<CartOrderEntity>(
                                "customer.customerId == $0",
                                customer.customerId
                            ).find()
                            val cart = this.query<CartEntity>(
                                "cartOrder.customer.customerId == $0",
                                customer.customerId
                            ).find()

                            if (cartOrder.isNotEmpty()) {
                                delete(cartOrder)
                            }

                            if (cart.isNotEmpty()) {
                                delete(cart)
                            }
                        }.also {
                            delete(customers)
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all customer")
        }
    }

    override suspend fun importContacts(customers: List<Customer>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    customers.forEach { customer ->
                        val findCustomer = this.query<CustomerEntity>(
                            "customerPhone == $0", customer.customerPhone
                        ).first().find()

                        if (findCustomer == null) {
                            val newCustomer = CustomerEntity()
                            newCustomer.customerId = BsonObjectId().toHexString()
                            newCustomer.customerName = customer.customerName
                            newCustomer.customerPhone = customer.customerPhone
                            newCustomer.customerEmail = customer.customerEmail
                            newCustomer.createdAt = System.currentTimeMillis().toString()

                            this.copyToRealm(newCustomer)

                        } else {
                            findCustomer.customerName = findCustomer.customerName
                            findCustomer.customerEmail = findCustomer.customerEmail
                            findCustomer.updatedAt = System.currentTimeMillis().toString()
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to import contacts")
        }
    }

    override fun validateCustomerName(customerName: String?): ValidationResult {
        if (!customerName.isNullOrEmpty()) {
            if (customerName.length < 3) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CUSTOMER_NAME_LENGTH_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerEmail(customerEmail: String?): ValidationResult {
        if (!customerEmail.isNullOrEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CUSTOMER_EMAIL_VALID_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerPhone(
        customerPhone: String,
        customerId: String?
    ): ValidationResult {
        if (customerPhone.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_EMPTY_ERROR
            )
        }

        if (customerPhone.length < 10 || customerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LENGTH_ERROR
            )
        }

        val containsLetters = customerPhone.any { it.isLetter() }

        if (containsLetters) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_LETTER_ERROR
            )
        }

        if (findCustomerByPhone(customerPhone, customerId)) {
            return ValidationResult(
                successful = false,
                errorMessage = CUSTOMER_PHONE_ALREADY_EXIST_ERROR
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun getCustomerWiseOrder(customerId: String): Flow<List<CustomerWiseOrder>> {
        return channelFlow {
            try {
                val orders = realm.query<CartOrderEntity>("customer.customerId == $0", customerId)
                    .sort("updatedAt", Sort.DESCENDING)
                    .asFlow()

                orders.collectLatest { result ->
                    when (result) {
                        is InitialResults -> {
                            send(mapCartOrdersToCustomerWiseOrder(result.list))
                        }

                        is UpdatedResults -> {
                            send(mapCartOrdersToCustomerWiseOrder(result.list))
                        }
                    }
                }

            } catch (e: Exception) {
                send(emptyList())
            }
        }
    }

    private fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder =
            realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
        val cartOrders = realm.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId).find()

        if (cartOrder != null && cartOrders.isNotEmpty()) {
            if (cartOrder.doesChargesIncluded) {
                val charges = realm.query<ChargesEntity>().find()
                for (charge in charges) {
                    if (charge.isApplicable && cartOrder.orderType != OrderType.DineIn.name) {
                        totalPrice += charge.chargesPrice
                    }
                }
            }

            if (cartOrder.addOnItems.isNotEmpty()) {
                for (addOnItem in cartOrder.addOnItems) {

                    totalPrice += addOnItem.itemPrice

                    if (!addOnItem.isApplicable) {
                        discountPrice += addOnItem.itemPrice
                    }
                }
            }

            for (cartOrder1 in cartOrders) {
                if (cartOrder1.product != null) {
                    totalPrice += cartOrder1.quantity.times(cartOrder1.product?.productPrice!!)
                }
            }
        }

        return Pair(totalPrice, discountPrice)
    }

    private fun mapCartOrdersToCustomerWiseOrder(data: List<CartOrderEntity>): List<CustomerWiseOrder> {
        return data.map { order ->
            val price = countTotalPrice(order.cartOrderId)
            val totalPrice = price.first.minus(price.second).toString()

            CustomerWiseOrder(
                cartOrderId = order.cartOrderId,
                orderId = order.orderId,
                totalPrice = totalPrice,
                updatedAt = order.updatedAt ?: order.createdAt,
                customerAddress = order.address?.addressName
            )
        }
    }
}