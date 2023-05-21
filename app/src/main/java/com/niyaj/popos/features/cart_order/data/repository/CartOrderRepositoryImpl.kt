package com.niyaj.popos.features.cart_order.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.utils.Constants.SELECTED_CART_ORDER_ID
import com.niyaj.popos.utils.getCalculatedStartDate
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CartOrderRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository,
    private val externalScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : CartOrderRepository, CartOrderValidationRepository {

    val realm = Realm.open(config)

    override fun getLastCreatedOrderId(): Long {
        return try {
            val cartOrderId = realm.query<CartOrder>().find().last().orderId.toLongOrNull()

            return cartOrderId ?: 0
        } catch (e: Exception) {
            0
        }
    }

    override suspend fun getAllCartOrders(viewAll: Boolean): Flow<Resource<List<CartOrder>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val items = if (viewAll){
                        realm.query<CartOrder>().sort("cartOrderId", Sort.DESCENDING)
                    }else{
                        realm.query<CartOrder>(
                            "cartOrderStatus == $0",
                            OrderStatus.Processing.orderStatus
                        ).sort("cartOrderId", Sort.DESCENDING)
                    }.asFlow()

                    items.collect { changes: ResultsChange<CartOrder> ->
                        when (changes) {
                            is UpdatedResults -> {
                                if (changes.list.isNotEmpty()) {
                                    if (changes.insertions.isNotEmpty()) {
                                        addSelectedCartOrder(changes.list[changes.insertions.last()].cartOrderId)
                                    } else if (changes.changes.isNotEmpty()) {
                                        addSelectedCartOrder(changes.list[changes.changes.last()].cartOrderId)
                                    } else {
                                        addSelectedCartOrder(changes.list.first().cartOrderId)
                                    }
                                } else {
                                    deleteSelectedCartOrder()
                                }

                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }

                            is InitialResults -> {

                                val selectedCartOrder =
                                    realm.query<SelectedCartOrder>().first().find()

                                if (changes.list.isNotEmpty()) {
                                    if (selectedCartOrder == null) {
                                        addSelectedCartOrder(changes.list.first().cartOrderId)
                                    }
                                } else {
                                    deleteSelectedCartOrder()
                                }

                                send(Resource.Success(changes.list))

                                send(Resource.Loading(false))
                            }
                        }
                    }

                } catch (e: Exception) {
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get cartOrders", emptyList()))
                }
            }
        }
    }

    override suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?> {
        return try {
            val cartOrder = withContext(ioDispatcher) {
                realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
            }

            Resource.Success(cartOrder)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Cart Order", null)
        }
    }

    override suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateOrderId = validateOrderId(newOrder.orderId)
                val validateAddress = validateCustomerAddress(newOrder.orderType, newOrder.address?.addressName ?: "")
                val validateCustomer = validateCustomerPhone(newOrder.orderType, newOrder.customer?.customerPhone ?: "")

                val hasError = listOf(validateAddress, validateCustomer, validateOrderId).any { !it.successful }

                if (!hasError) {
                    var customer: Customer? = null

                    var address: Address? = null

                    if (newOrder.customer != null) {
                        val findCustomer = realm.query<Customer>(
                            "customerPhone == $0",
                            newOrder.customer!!.customerPhone
                        ).first().find()

                        if (findCustomer == null) {
                            val newCustomer = Customer()
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
                            realm.query<Address>("addressId == $0", newOrder.address!!.addressId)
                                .first().find()
                        } else {
                            realm.query<Address>("addressName == $0", newOrder.address!!.addressName)
                                .first().find()
                        }

                        if (findAddress == null) {
                            val newAddress = Address()
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

                    val cartOrder = CartOrder()
                    cartOrder.cartOrderId = newOrder.cartOrderId.ifEmpty { BsonObjectId().toHexString() }
                    cartOrder.orderId = newOrder.orderId
                    cartOrder.orderType = newOrder.orderType
                    cartOrder.createdAt = newOrder.createdAt.ifEmpty { System.currentTimeMillis().toString() }

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
                    Resource.Error("Unable to validate cart order", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create order", false)
        }
    }

    override suspend fun updateCartOrder(newOrder: CartOrder, cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val validateOrderId = validateOrderId(newOrder.orderId)
                val validateAddress = validateCustomerAddress(newOrder.orderType, newOrder.address?.addressName ?: "")
                val validateCustomer = validateCustomerPhone(newOrder.orderType, newOrder.customer?.customerPhone ?: "")

                val hasError = listOf(validateAddress, validateCustomer, validateOrderId).any { !it.successful }

                if (!hasError) {
                    val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    if (cartOrder != null) {
                        var customer: Customer? = null

                        var address: Address? = null

                        if (newOrder.customer != null) {
                            val findCustomer = realm.query<Customer>("customerPhone == $0", newOrder.customer!!.customerPhone).first().find()

                            if (findCustomer == null) {
                                val newCustomer = Customer()
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
                                realm.query<Address>("addressId == $0", newOrder.address!!.addressId).first().find()
                            } else {
                                realm.query<Address>("addressName == $0", newOrder.address!!.addressName).first().find()
                            }

                            if (findAddress == null) {
                                val newAddress = Address()
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
                                this.orderType = newOrder.orderType
                                this.updatedAt = System.currentTimeMillis().toString()

                                if (newOrder.orderType == CartOrderType.DineOut.orderType) {
                                    if (customer != null){
                                        findLatest(customer).also { this.customer = it }
                                    }
                                } else {
                                    this.customer = null
                                }

                                if (newOrder.orderType == CartOrderType.DineOut.orderType) {
                                    if (address != null) {
                                        findLatest(address).also { this.address = it }
                                    }
                                } else {
                                    this.address = null
                                }
                            }
                        }

                        Resource.Success(true)
                    }else {
                        Resource.Error("Unable to find cart order", false)
                    }

                }else {
                    Resource.Error("Unable to validate cart order", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update order", false)
        }
    }

    override suspend fun updateAddOnItem(addOnItemId: String, cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                val addOnItem = realm.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()
                if (addOnItem != null) {
                    val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()

                    if (cartOrder != null) {
                        realm.write {
                            val newAddOnItem = this.query<CartOrder>("cartOrderId == $0", cartOrderId).find()
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
                    }else {
                        Resource.Error("Unable to find cart order", false)
                    }
                }else {
                    Resource.Error("Unable to find add-on item", false)
                }

            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add add on item", false)
        }
    }

    override suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                if (cartOrder != null) {
                    realm.write {
                        val cartProducts = this.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

                        delete(cartProducts)

                        findLatest(cartOrder)?.let {
                            delete(it)
                        }
                    }

                    removeAndSetSelectedCartOrder()

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find cart order", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart", false)
        }
    }

    override suspend fun placeOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                if (cartOrder != null) {
                    realm.write {
                        findLatest(cartOrder)?.apply {
                            this.cartOrderStatus = OrderStatus.Placed.orderStatus
                            this.updatedAt = System.currentTimeMillis().toString()
                        }
                    }
                    removeAndSetSelectedCartOrder()

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find cart order", false)
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place order", false)
        }
    }

    override suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                realm.write {
                    for (cartOrderId in cartOrderIds) {
                        val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                        cartOrder?.cartOrderStatus = OrderStatus.Placed.orderStatus
                        cartOrder?.updatedAt = System.currentTimeMillis().toString()
                    }
                }

                removeAndSetSelectedCartOrder()

                Resource.Success(true)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place all cart orders", false)
        }
    }

    override suspend fun getSelectedCartOrders(): Flow<SelectedCartOrder?> {
        return channelFlow {
            withContext(ioDispatcher){
                val selectedCartOrder = realm.query<SelectedCartOrder>("selectedCartId == $0", SELECTED_CART_ORDER_ID).first().asFlow()

                selectedCartOrder.collect { changes ->
                    when (changes) {
                        is InitialObject -> {
                            send(changes.obj)
                        }
                        is UpdatedObject -> {
                            send(changes.obj)
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
            withContext(ioDispatcher){
                val order = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()

                if (order != null && order.cartOrderStatus != OrderStatus.Placed.orderStatus) {
                    realm.write {
                        val selectedCartOrder = this.query<SelectedCartOrder>("selectedCartId == $0",
                            SELECTED_CART_ORDER_ID
                        ).first().find()

                        if (selectedCartOrder != null) {
                            findLatest(order)?.let {
                                selectedCartOrder.cartOrder = it
                            }
                        }else {
                            val cartOrder = SelectedCartOrder()
                            findLatest(order)?.let {
                                cartOrder.cartOrder = it
                            }

                            this.copyToRealm(cartOrder, UpdatePolicy.ALL)
                        }
                    }
                    true
                }else {
                    false
                }
            }
        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteSelectedCartOrder(): Boolean {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val selectedCartOrder = this.query<SelectedCartOrder>().find()
                    delete(selectedCartOrder)
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
                    realm.query<CartOrder>().find()
                } else {
                    realm.query<CartOrder>("createdAt < $0", cartOrderDate).find()
                }

                if (cartOrders.isNotEmpty()) {
                    cartOrders.forEach { cartOrderRealm ->
                        deleteCartOrder(cartOrderRealm.cartOrderId)
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete all cart orders", false)
        }
    }

    override fun validateCustomerAddress(orderType: String, customerAddress: String): ValidationResult {
        if(orderType != CartOrderType.DineIn.orderType) {
            if(customerAddress.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer address must not be empty"
                )
            }
            if(customerAddress.length < 2) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "The address must be more than 2 characters long"
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateCustomerPhone(orderType: String, customerPhone: String): ValidationResult {
        if(orderType != CartOrderType.DineIn.orderType){
            if(customerPhone.isEmpty()){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Phone no must not be empty",
                )
            }

            if(customerPhone.length != 10) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "The phone no must be 10 digits long"
                )
            }

            val containsLetters = customerPhone.any { it.isLetter() }

            if(containsLetters){
                return ValidationResult(
                    successful = false,
                    errorMessage = "The phone no does not contains any characters"
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateOrderId(orderId: String): ValidationResult {
        if(orderId.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "The order id must not be empty"
            )
        }

//        if(orderId.length < 6) {
//            return ValidationResult(
//                successful = false,
//                errorMessage = "The order id must be 6 characters long"
//            )
//        }

        return ValidationResult(
            successful = true
        )
    }

    private suspend fun removeAndSetSelectedCartOrder(): Boolean {
        try {
            withContext(ioDispatcher) {
                realm.write {
                    val selectedCartOrder = this.query<SelectedCartOrder>().first().find()

                    val cartOrder = this.query<CartOrder>("cartOrderStatus == $0",
                        OrderStatus.Processing.orderStatus
                    ).find()

                    if (cartOrder.isNotEmpty()){
                        val lastOrder = cartOrder.last()

                        selectedCartOrder?.cartOrder = lastOrder
                    }else {
                        delete(SelectedCartOrder::class)
                    }
                }
            }

            return true
        }catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }
}