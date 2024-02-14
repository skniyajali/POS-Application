package com.niyaj.data.data.repository

import com.niyaj.common.tags.CartOrderTestTags.ADDRESS_NAME_LENGTH_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NAME_EMPTY_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_PHONE_EMPTY_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LENGTH_ERROR
import com.niyaj.common.tags.CartOrderTestTags.CUSTOMER_PHONE_LETTER_ERROR
import com.niyaj.common.utils.Constants.SELECTED_CART_ORDER_ID
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.common.utils.getCalculatedStartDate
import com.niyaj.data.repository.CartOrderRepository
import com.niyaj.data.repository.SettingsRepository
import com.niyaj.data.repository.validation.CartOrderValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.AddressEntity
import com.niyaj.database.model.CartEntity
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.CustomerEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.Address
import com.niyaj.model.CartOrder
import com.niyaj.model.Customer
import com.niyaj.model.OrderStatus
import com.niyaj.model.OrderType
import com.niyaj.model.filterAddress
import com.niyaj.model.filterCustomer
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CartOrderRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val ioDispatcher: CoroutineDispatcher,
) : CartOrderRepository, CartOrderValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getLastCreatedOrderId(cartOrderId: String): String {
        return withContext(ioDispatcher) {
            if (cartOrderId.isEmpty()) {
                val orderId = realm
                    .query<CartOrderEntity>()
                    .sort("cartOrderId", Sort.DESCENDING)
                    .limit(1)
                    .first()
                    .find()
                    ?.orderId

                orderId?.toInt()?.plus(1)?.toString() ?: "1"
            } else {
                realm
                    .query<CartOrderEntity>("cartOrderId == $0", cartOrderId)
                    .sort("cartOrderId", Sort.DESCENDING)
                    .limit(1)
                    .first()
                    .find()
                    ?.orderId ?: "1"
            }
        }
    }

    override suspend fun getAllCartOrders(
        viewAll: Boolean
    ): Flow<List<CartOrder>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val cartOrders = withContext(ioDispatcher) {
                        if (viewAll) {
                            realm.query<CartOrderEntity>().sort("cartOrderId", Sort.DESCENDING)
                        } else {
                            realm.query<CartOrderEntity>(
                                "cartOrderStatus == $0",
                                OrderStatus.PROCESSING.name
                            ).sort("cartOrderId", Sort.DESCENDING)
                        }.asFlow()
                    }

                    cartOrders.collectLatest { items: ResultsChange<CartOrderEntity> ->
                        when (items) {
                            is UpdatedResults -> {
                                if (items.list.isNotEmpty()) {
                                    if (items.insertions.isNotEmpty()) {
                                        addSelectedCartOrder(items.list[items.insertions.last()].cartOrderId)
                                    } else if (items.changes.isNotEmpty()) {
                                        addSelectedCartOrder(items.list[items.changes.last()].cartOrderId)
                                    } else {
                                        addSelectedCartOrder(items.list.first().cartOrderId)
                                    }
                                } else {
                                    deleteSelectedCartOrder()
                                }

                                send(items.list.map { it.toExternalModel() })
                            }

                            is InitialResults -> {
                                val selectedCartOrder =
                                    realm.query<SelectedCartOrderEntity>().first().find()

                                if (items.list.isNotEmpty()) {
                                    if (selectedCartOrder == null) {
                                        addSelectedCartOrder(items.list.first().cartOrderId)
                                    }
                                } else {
                                    deleteSelectedCartOrder()
                                }

                                send(items.list.map { it.toExternalModel() })
                            }
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getAllAddress(searchText: String): Flow<List<Address>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val items = realm.query<AddressEntity>()
                        .sort("addressId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    items.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.filterAddress(searchText) },
                        send = { send(it) }
                    )

                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
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

    override suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?> {
        return try {
            val cartOrder = withContext(ioDispatcher) {
                realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
            }

            Resource.Success(cartOrder?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Cart Order")
        }
    }

    override suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateOrderId = validateOrderId(newOrder.orderId, newOrder.cartOrderId)
                val validateAddress = validateCustomerAddress(
                    newOrder.orderType,
                    newOrder.address?.addressName ?: ""
                )
                val validateCustomer = validateCustomerPhone(
                    newOrder.orderType,
                    newOrder.customer?.customerPhone ?: ""
                )

                val hasError = listOf(
                    validateAddress,
                    validateCustomer,
                    validateOrderId
                ).any { !it.successful }

                if (!hasError) {
                    var customer: CustomerEntity? = null

                    var address: AddressEntity? = null

                    if (newOrder.customer != null) {
                        val findCustomer = realm.query<CustomerEntity>(
                            "customerPhone == $0",
                            newOrder.customer!!.customerPhone
                        ).first().find()

                        if (findCustomer == null) {
                            val newCustomer = CustomerEntity()
                            newCustomer.customerId = BsonObjectId().toHexString()
                            newCustomer.customerPhone = newOrder.customer!!.customerPhone
                            newCustomer.createdAt = System.currentTimeMillis().toString()

                            customer = realm.write {
                                this.copyToRealm(newCustomer)
                            }
                        } else {
                            customer = findCustomer
                        }
                    }

                    if (newOrder.address != null) {

                        val findAddress = if (newOrder.address!!.addressId.isNotEmpty()) {
                            realm.query<AddressEntity>(
                                "addressId == $0",
                                newOrder.address!!.addressId
                            )
                                .first().find()
                        } else {
                            realm.query<AddressEntity>(
                                "addressName == $0",
                                newOrder.address!!.addressName
                            )
                                .first().find()
                        }

                        if (findAddress == null) {
                            val newAddress = AddressEntity()
                            newAddress.addressId = BsonObjectId().toHexString()
                            newAddress.shortName = newOrder.address!!.shortName
                            newAddress.addressName = newOrder.address!!.addressName
                            newAddress.createdAt = System.currentTimeMillis().toString()

                            address = realm.write {
                                this.copyToRealm(newAddress)
                            }

                        } else {
                            address = findAddress
                        }
                    }

                    val cartOrder = CartOrderEntity()
                    cartOrder.cartOrderId =
                        newOrder.cartOrderId.ifEmpty { BsonObjectId().toHexString() }
                    cartOrder.orderId = newOrder.orderId
                    cartOrder.orderType = newOrder.orderType.name
                    cartOrder.createdAt =
                        newOrder.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        if (customer != null) {
                            findLatest(customer)?.also {
                                cartOrder.customer = it
                            }
                        }

                        if (address != null) {
                            findLatest(address)?.also {
                                cartOrder.address = it
                            }
                        }

                        this.copyToRealm(cartOrder)
                    }

                    addSelectedCartOrder(cartOrder.cartOrderId)

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to validate cart order")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create order")
        }
    }

    override suspend fun updateCartOrder(
        newOrder: CartOrder,
        cartOrderId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val validateOrderId = validateOrderId(newOrder.orderId, cartOrderId)
                val validateAddress =
                    validateCustomerAddress(newOrder.orderType, newOrder.address?.addressName ?: "")
                val validateCustomer = validateCustomerPhone(
                    newOrder.orderType,
                    newOrder.customer?.customerPhone ?: ""
                )

                val hasError = listOf(
                    validateAddress,
                    validateCustomer,
                    validateOrderId
                ).any { !it.successful }

                if (!hasError) {
                    val cartOrder =
                        realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first()
                            .find()
                    if (cartOrder != null) {
                        var customer: CustomerEntity? = null

                        var address: AddressEntity? = null

                        if (newOrder.customer != null) {
                            val findCustomer = realm.query<CustomerEntity>(
                                "customerPhone == $0",
                                newOrder.customer!!.customerPhone
                            ).first().find()

                            if (findCustomer == null) {
                                val newCustomer = CustomerEntity()
                                newCustomer.customerId = BsonObjectId().toHexString()
                                newCustomer.customerPhone = newOrder.customer!!.customerPhone
                                newCustomer.createdAt = System.currentTimeMillis().toString()

                                customer = realm.write {
                                    this.copyToRealm(newCustomer)
                                }
                            } else {
                                customer = findCustomer
                            }
                        }

                        if (newOrder.address != null) {

                            val findAddress = if (newOrder.address!!.addressId.isNotEmpty()) {
                                realm.query<AddressEntity>(
                                    "addressId == $0",
                                    newOrder.address!!.addressId
                                ).first().find()
                            } else {
                                realm.query<AddressEntity>(
                                    "addressName == $0",
                                    newOrder.address!!.addressName
                                ).first().find()
                            }

                            if (findAddress == null) {
                                val newAddress = AddressEntity()
                                newAddress.addressId = BsonObjectId().toHexString()
                                newAddress.shortName = newOrder.address!!.shortName
                                newAddress.addressName = newOrder.address!!.addressName
                                newAddress.createdAt = System.currentTimeMillis().toString()

                                address = realm.write {
                                    this.copyToRealm(newAddress)
                                }

                            } else {
                                address = findAddress
                            }
                        }

                        realm.write {
                            findLatest(cartOrder)?.apply {
                                this.orderId = newOrder.orderId
                                this.orderType = newOrder.orderType.name
                                this.updatedAt = System.currentTimeMillis().toString()

                                if (newOrder.orderType == OrderType.DineOut) {
                                    if (customer != null) {
                                        findLatest(customer).also { this.customer = it }
                                    }
                                } else {
                                    this.customer = null
                                }

                                if (newOrder.orderType == OrderType.DineOut) {
                                    if (address != null) {
                                        findLatest(address).also { this.address = it }
                                    }
                                } else {
                                    this.address = null
                                }
                            }
                        }

                        Resource.Success(true)
                    } else {
                        Resource.Error("Unable to find cart order")
                    }

                } else {
                    Resource.Error("Unable to validate cart order")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update order")
        }
    }

    override suspend fun updateAddOnItem(
        addOnItemId: String,
        cartOrderId: String
    ): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val addOnItem =
                    realm.query<AddOnItemEntity>("addOnItemId == $0", addOnItemId).first().find()
                if (addOnItem != null) {
                    val cartOrder =
                        realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first()
                            .find()

                    if (cartOrder != null) {
                        realm.write {
                            val newAddOnItem =
                                this.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).find()
                                    .first().addOnItems

                            val doesExist = newAddOnItem.find { it.addOnItemId == addOnItemId }

                            if (doesExist == null) {
                                findLatest(addOnItem)?.let {
                                    newAddOnItem.add(it)
                                }
                            } else {
                                newAddOnItem.removeIf { it.addOnItemId == addOnItemId }
                            }

                            findLatest(cartOrder)?.apply {
                                this.addOnItems = newAddOnItem
                            }
                        }

                        Resource.Success(true)
                    } else {
                        Resource.Error("Unable to find cart order")
                    }
                } else {
                    Resource.Error("Unable to find add-on item")
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add add on item")
        }
    }

    override suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartOrder =
                    realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
                if (cartOrder != null) {
                    realm.write {
                        val cartProducts =
                            this.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId)
                                .find()

                        delete(cartProducts)

                        findLatest(cartOrder)?.let {
                            delete(it)
                        }
                    }

                    removeAndSetSelectedCartOrder()

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find cart order")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart")
        }
    }

    override suspend fun deleteCartOrders(cartOrderIds: List<String>): Resource<Boolean> {
        return try {
            cartOrderIds.forEach { cartOrderId ->
                withContext(ioDispatcher) {
                    val cartOrder =
                        realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first()
                            .find()
                    if (cartOrder != null) {
                        realm.write {
                            val cartProducts =
                                this.query<CartEntity>("cartOrder.cartOrderId == $0", cartOrderId)
                                    .find()

                            delete(cartProducts)

                            findLatest(cartOrder)?.let {
                                delete(it)
                            }
                        }

                        removeAndSetSelectedCartOrder()
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable")
        }
    }

    override suspend fun placeOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartOrder =
                    realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()
                if (cartOrder != null) {
                    realm.write {
                        findLatest(cartOrder)?.apply {
                            this.cartOrderStatus = OrderStatus.PLACED.name
                            this.updatedAt = System.currentTimeMillis().toString()
                        }
                    }
                    removeAndSetSelectedCartOrder()

                    Resource.Success(true)
                } else {
                    Resource.Error("Unable to find cart order")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place order")
        }
    }

    override suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    for (cartOrderId in cartOrderIds) {
                        val cartOrder =
                            this.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first()
                                .find()
                        cartOrder?.cartOrderStatus = OrderStatus.PLACED.name
                        cartOrder?.updatedAt = System.currentTimeMillis().toString()
                    }
                }

                removeAndSetSelectedCartOrder()

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place all cart orders")
        }
    }

    override suspend fun getSelectedCartOrders(): Flow<String?> {
        return channelFlow {
            withContext(ioDispatcher) {
                val selectedCartOrder = realm
                    .query<SelectedCartOrderEntity>("selectedCartId == $0", SELECTED_CART_ORDER_ID)
                    .first()
                    .asFlow()

                selectedCartOrder.collect { changes ->
                    when (changes) {
                        is InitialObject -> {
                            send(changes.obj.cartOrder?.cartOrderId)
                        }

                        is UpdatedObject -> {
                            send(changes.obj.cartOrder?.cartOrderId)
                        }

                        else -> {
                            send(null)
                        }
                    }
                }
            }
        }
    }

    override suspend fun addSelectedCartOrder(cartOrderId: String): Boolean {
        return try {
            withContext(ioDispatcher) {
                val order =
                    realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()

                if (order != null && order.cartOrderStatus != OrderStatus.PLACED.name) {
                    realm.write {
                        val selectedCartOrder = this.query<SelectedCartOrderEntity>(
                            "selectedCartId == $0",
                            SELECTED_CART_ORDER_ID
                        ).first().find()

                        if (selectedCartOrder != null) {
                            findLatest(order)?.let {
                                selectedCartOrder.cartOrder = it
                            }
                        } else {
                            val cartOrder = SelectedCartOrderEntity()
                            findLatest(order)?.let {
                                cartOrder.cartOrder = it
                            }

                            this.copyToRealm(cartOrder, UpdatePolicy.ALL)
                        }
                    }
                    true
                } else {
                    false
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteSelectedCartOrder(): Boolean {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    delete(SelectedCartOrderEntity::class)
                }
            }

            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteCartOrders(deleteAll: Boolean): Resource<Boolean> {
        return try {
            val settings = settingsRepository.getSetting().data!!
            val cartOrderDate =
                getCalculatedStartDate(days = "-${settings.cartOrderDataDeletionInterval}")

            withContext(ioDispatcher) {
                val cartOrders = if (deleteAll) {
                    realm.query<CartOrderEntity>().find()
                } else {
                    realm.query<CartOrderEntity>("createdAt < $0", cartOrderDate).find()
                }

                if (cartOrders.isNotEmpty()) {
                    cartOrders.forEach { cartOrderRealm ->
                        deleteCartOrder(cartOrderRealm.cartOrderId)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all cart orders")
        }
    }

    override fun validateCustomerAddress(
        orderType: OrderType,
        customerAddress: String
    ): ValidationResult {
        if (orderType != OrderType.DineIn) {
            if (customerAddress.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_NAME_EMPTY_ERROR
                )
            }
            if (customerAddress.length < 2) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDRESS_NAME_LENGTH_ERROR
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerPhone(
        orderType: OrderType,
        customerPhone: String
    ): ValidationResult {
        if (orderType != OrderType.DineIn) {
            if (customerPhone.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = CART_ORDER_PHONE_EMPTY_ERROR
                )
            }

            if (customerPhone.length != 10) {
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
        }

        return ValidationResult(
            successful = true
        )
    }

    override suspend fun validateOrderId(orderId: String, cartOrderId: String): ValidationResult {
        if (orderId.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "The order id must not be empty"
            )
        }

        val findOrderId = withContext(ioDispatcher) {
            if (cartOrderId.isEmpty()) {
                realm.query<CartOrderEntity>("orderId == $0", orderId).first().find()
            } else {
                realm.query<CartOrderEntity>(
                    "orderId == $0 && cartOrderId != $1",
                    orderId,
                    cartOrderId
                ).first().find()
            }
        }

        if (findOrderId != null) {
            return ValidationResult(
                successful = false,
                errorMessage = "The order id must not be duplicate"
            )
        }

        return ValidationResult(
            successful = true
        )
    }

    private suspend fun removeAndSetSelectedCartOrder(): Boolean {
        try {
            withContext(ioDispatcher) {
                realm.write {
                    val selectedCartOrder = this.query<SelectedCartOrderEntity>().first().find()

                    val cartOrder = this.query<CartOrderEntity>(
                        "cartOrderStatus == $0",
                        OrderStatus.PROCESSING.name
                    ).find()

                    if (cartOrder.isNotEmpty()) {
                        val lastOrder = cartOrder.last()

                        selectedCartOrder?.cartOrder = lastOrder
                    } else {
                        delete(SelectedCartOrderEntity::class)
                    }
                }
            }

            return true
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }
}