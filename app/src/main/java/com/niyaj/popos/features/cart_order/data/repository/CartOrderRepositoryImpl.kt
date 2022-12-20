package com.niyaj.popos.features.cart_order.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart.domain.model.CartRealm
import com.niyaj.popos.features.cart_order.domain.model.CartOrder
import com.niyaj.popos.features.cart_order.domain.model.SelectedCartOrder
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.cart_order.domain.util.OrderStatus
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.internal.interop.RealmCoreException
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CartOrderRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository
) : CartOrderRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("CartOrder Session")
    }

    /**
     * Get last created cart order ID [Long] else [0]
     */
    override fun getLastCreatedOrderId(): Long {
        return try {
            val cartOrderId = realm.query<CartOrder>().find().last().orderId.toLongOrNull()

            return cartOrderId ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get all [CartOrder] that wraps with [Resource].
     *
     */
    override suspend fun getAllCartOrders(): Flow<Resource<List<CartOrder>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<CartOrder>(
                    "cartOrderStatus == $0",
                    OrderStatus.Processing.orderStatus
                ).sort("cartOrderId", Sort.DESCENDING)

                val itemsFlow = items.asFlow()

                itemsFlow.collect { changes: ResultsChange<CartOrder> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(Resource.Success(changes.list))

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

                            send(Resource.Loading(false))
                        }

                        is InitialResults -> {

                            send(Resource.Success(changes.list))

                            val selectedCartOrder =
                                realm.query<SelectedCartOrder>().first().find()

                            if (changes.list.isNotEmpty()) {
                                if (selectedCartOrder == null) {
                                    addSelectedCartOrder(changes.list.first().cartOrderId)
                                }
                            } else {
                                deleteSelectedCartOrder()
                            }

                            send(Resource.Loading(false))
                        }
                    }
                }
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get cartOrders"))
            }
        }
    }

    override suspend fun getCartOrderById(cartOrderId: String): Resource<CartOrder?> {
        return try {
            val cartOrder = realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
            Resource.Success(cartOrder)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Cart Order", null)
        }
    }

    override suspend fun createNewOrder(newOrder: CartOrder): Resource<Boolean> {
        return try {
            var customer: Customer? = null

            var address: Address? = null

            if (newOrder.customer != null) {
                if (newOrder.customer!!.customerId.isEmpty() && newOrder.customer!!.customerPhone.isNotEmpty()) {
                    val newCustomer = Customer()
                    newCustomer.customerId = BsonObjectId().toHexString()
                    newCustomer.customerPhone = newOrder.customer!!.customerPhone
                    newCustomer.createdAt = System.currentTimeMillis().toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            customer = this.copyToRealm(newCustomer)
                        }
                    }

                } else {
                    customer =
                        realm.query<Customer>("customerId == $0", newOrder.customer!!.customerId)
                            .first().find()
                }
            }

            if (newOrder.address != null) {
                if (newOrder.address!!.addressId.isEmpty() && newOrder.address!!.addressName.isNotEmpty()) {
                    val newAddress = Address()
                    newAddress.addressId = BsonObjectId().toHexString()
                    newAddress.shortName = newOrder.address!!.shortName
                    newAddress.addressName = newOrder.address!!.addressName
                    newAddress.createdAt = System.currentTimeMillis().toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            address = this.copyToRealm(newAddress)
                        }
                    }
                } else {
                    address =
                        realm.query<Address>("addressId == $0", newOrder.address!!.addressId)
                            .first().find()
                }
            }

            val cartOrder = CartOrder()
            cartOrder.cartOrderId = BsonObjectId().toHexString()
            cartOrder.orderId = newOrder.orderId
            cartOrder.orderType = newOrder.orderType
            cartOrder.createdAt = System.currentTimeMillis().toString()

            realm.write {

                if (customer != null) {
                    findLatest(customer!!).also { cartOrder.customer = it }
                }

                if (address != null) {
                    findLatest(address!!).also { cartOrder.address = it }
                }

                this.copyToRealm(cartOrder)

            }

            this.addSelectedCartOrder(cartOrder.cartOrderId)

            Resource.Success(true)
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Unable to create cart order", false)
        } catch (e: RealmCoreException) {
            Resource.Error(e.message ?: "Unable to create cart order", false)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create cart order", false)
        }
    }

    override suspend fun updateCartOrder(
        newOrder: CartOrder,
        cartOrderId: String,
    ): Resource<Boolean> {
        return try {
            var customer: Customer? = null

            var address: Address? = null

            if (newOrder.customer != null) {

                if (newOrder.customer!!.customerId.isEmpty() && newOrder.customer!!.customerPhone.isNotEmpty()) {
                    val newCustomer = Customer()
                    newCustomer.customerId = BsonObjectId().toHexString()
                    newCustomer.customerPhone = newOrder.customer!!.customerPhone
                    newCustomer.createdAt = System.currentTimeMillis().toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            customer = this.copyToRealm(newCustomer)
                        }
                    }

                } else {
                    customer =
                        realm.query<Customer>("customerId == $0", newOrder.customer!!.customerId)
                            .first().find()
                }
            }

            if (newOrder.address != null) {
                if (newOrder.address!!.addressId.isEmpty() && newOrder.address!!.addressName.isNotEmpty()) {
                    val newAddress = Address()
                    newAddress.addressId = BsonObjectId().toHexString()
                    newAddress.shortName = newOrder.address!!.shortName
                    newAddress.addressName = newOrder.address!!.addressName
                    newAddress.createdAt = System.currentTimeMillis().toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        realm.write {
                            address = this.copyToRealm(newAddress)
                        }
                    }
                } else {
                    address = realm.query<Address>("addressId == $0", newOrder.address!!.addressId)
                        .first().find()
                }
            }

            realm.write {
                val cartOrder = this.query<CartOrder>("addressId == $0", cartOrderId).first().find()
                cartOrder?.orderId = newOrder.orderId
                cartOrder?.orderType = newOrder.orderType
                cartOrder?.updatedAt = System.currentTimeMillis().toString()

                if (customer != null) {
                    findLatest(customer!!).also { cartOrder?.customer = it }
                } else if (newOrder.orderType == CartOrderType.DineIn.orderType) {
                    cartOrder?.customer = null
                } else {
                    cartOrder?.customer = null
                }

                if (address != null) {
                    findLatest(address!!).also { cartOrder?.address = it }
                } else if (newOrder.orderType != CartOrderType.DineIn.orderType) {
                    cartOrder?.address = null
                } else {
                    cartOrder?.address = null
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update order", false)
        }
    }

    override suspend fun updateAddOnItem(
        addOnItemId: String,
        cartOrderId: String,
    ): Resource<Boolean> {
        return try {
            realm.write {
                val addOnItem =
                    this.query<AddOnItem>("addOnItemId == $0", addOnItemId).find().first()

                val newAddOnItem =
                    this.query<CartOrder>("cartOrderId == $0", cartOrderId).find()
                        .first().addOnItems

                val doesExist = newAddOnItem.find { it.addOnItemId == addOnItemId }

                val cartOrder =
                    this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()


                if (doesExist == null) {
                    newAddOnItem.add(addOnItem)
                } else {
                    newAddOnItem.removeIf { it.addOnItemId == addOnItemId }
                }

                cartOrder?.addOnItems = newAddOnItem

            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add add on item", false)
        }
    }

    override suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            CoroutineScope(Dispatchers.Default).launch {
                realm.write {
                    val cartOrder =
                        this.query<CartOrder>("cartOrderId == $0", cartOrderId).find().first()
                    val cartProducts: RealmResults<CartRealm> =
                        this.query<CartRealm>("cartOrder.cartId == $0", cartOrderId).find()

                    delete(cartProducts)
                    delete(cartOrder)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete cart", false)
        }
    }

    override suspend fun placeOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            realm.write {
                val cartOrder =
                    this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                cartOrder?.cartOrderStatus = OrderStatus.Placed.orderStatus
                cartOrder?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place order", false)
        }
    }

    override suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean> {
        return try {
            realm.write {
                for (cartOrderId in cartOrderIds) {
                    val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    cartOrder?.cartOrderStatus = OrderStatus.Placed.orderStatus
                    cartOrder?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place all cart orders", false)
        }
    }

    override suspend fun getSelectedCartOrders(): Flow<SelectedCartOrder?> {
        val selectedCartOrder = realm.query<SelectedCartOrder>().first()
        val cartOrders = MutableStateFlow<SelectedCartOrder?>(null)

        CoroutineScope(Dispatchers.Default).launch {
            val items = selectedCartOrder.asFlow()
            items.collect { changes ->
                when (changes) {
                    is InitialObject -> {
                        cartOrders.emit(changes.obj)
                    }

                    is UpdatedObject -> {
                        cartOrders.emit(changes.obj)
                    }

                    else -> {
                        cartOrders.emit(changes.obj)
                    }
                }
            }
        }

        return cartOrders
    }

    override suspend fun addSelectedCartOrder(cartOrderId: String): Boolean {
        try {
            if (cartOrderId.isNotEmpty()) {
                val cartOrder =
                    realm.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()

                realm.write {
                    if (cartOrder != null) {
                        val selectedCartOrder =
                            this.query<SelectedCartOrder>().first().find()

                        if (selectedCartOrder != null) {

                            if (selectedCartOrder.cartOrder?.cartOrderId != cartOrderId) {
                                findLatest(cartOrder)?.also {
                                    selectedCartOrder.cartOrder = it
                                }

                                this.copyToRealm(selectedCartOrder)
                            }
                        } else {
                            val newSelectedCartOrder = SelectedCartOrder()

                            findLatest(cartOrder)?.also {
                                newSelectedCartOrder.cartOrder = it
                            }

                            this.copyToRealm(newSelectedCartOrder)
                        }
                    }
                }

                return true
            }

            return false
        } catch (e: Exception) {
            Timber.e(e)
            return false
        }
    }

    override suspend fun deleteSelectedCartOrder(): Boolean {
        return try {
            realm.write {
                val selectedCartOrder = this.query<SelectedCartOrder>().find()
                delete(selectedCartOrder)
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


            CoroutineScope(Dispatchers.IO).launch {
                val cartOrders = if (deleteAll) {
                    realm.query<CartOrder>().find()
                } else {
                    realm.query<CartOrder>("created_at < $0", cartOrderDate).find()
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
}