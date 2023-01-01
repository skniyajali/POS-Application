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
import com.niyaj.popos.util.Constants.SELECTED_CART_ORDER_ID
import com.niyaj.popos.util.getCalculatedStartDate
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
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
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class CartOrderRepositoryImpl(
    config: RealmConfiguration,
    private val settingsRepository: SettingsRepository
) : CartOrderRepository {

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
            withContext(Dispatchers.IO){
                try {
                    send(Resource.Loading(true))

                    val items = if (viewAll){
                        realm.query<CartOrder>().sort("cartOrderId", Sort.DESCENDING)
                    }else{
                        realm.query<CartOrder>(
                            "cartOrderStatus == $0",
                            OrderStatus.Processing.orderStatus
                        ).sort("cartOrderId", Sort.DESCENDING)
                    }

                    val itemsFlow = items.asFlow()

                    itemsFlow.collect { changes: ResultsChange<CartOrder> ->
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
            cartOrder.cartOrderId = BsonObjectId().toHexString()
            cartOrder.orderId = newOrder.orderId
            cartOrder.orderType = newOrder.orderType
            cartOrder.createdAt = System.currentTimeMillis().toString()

            withContext(Dispatchers.IO){
                realm.write {
                    if (customer != null) {
                        Timber.d("customer: ${customer.customerPhone}")

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
            }

            this.addSelectedCartOrder(cartOrder.cartOrderId)

            Resource.Success(true)
        }catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to create order", false)
        }
    }

    override suspend fun updateCartOrder(
        newOrder: CartOrder,
        cartOrderId: String,
    ): Resource<Boolean> {
        return try {
            withContext(Dispatchers.IO){
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
                    val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    cartOrder?.orderId = newOrder.orderId
                    cartOrder?.orderType = newOrder.orderType
                    cartOrder?.updatedAt = System.currentTimeMillis().toString()

                    if (newOrder.orderType == CartOrderType.DineOut.orderType) {
                        if (customer != null){
                            findLatest(customer).also { cartOrder?.customer = it }
                        }
                    } else {
                        cartOrder?.customer = null
                    }

                    if (newOrder.orderType == CartOrderType.DineOut.orderType) {
                        if (address != null) {
                            findLatest(address).also { cartOrder?.address = it }
                        }
                    } else {
                        cartOrder?.address = null
                    }
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
            withContext(Dispatchers.IO){
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
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to add add on item", false)
        }
    }

    override suspend fun deleteCartOrder(cartOrderId: String): Resource<Boolean> {
        return try {
            CoroutineScope(Dispatchers.IO).launch {
                realm.write {
                    val cartOrder =
                        this.query<CartOrder>("cartOrderId == $0", cartOrderId).find().first()
                    val cartProducts: RealmResults<CartRealm> =
                        this.query<CartRealm>("cartOrder.cartOrderId == $0", cartOrderId).find()

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
            withContext(Dispatchers.IO){
                realm.write {
                    val cartOrder =
                        this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    cartOrder?.cartOrderStatus = OrderStatus.Placed.orderStatus
                    cartOrder?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place order", false)
        }
    }

    override suspend fun placeAllOrder(cartOrderIds: List<String>): Resource<Boolean> {
        return try {
            withContext(Dispatchers.IO){
                realm.write {
                    for (cartOrderId in cartOrderIds) {
                        val cartOrder = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                        cartOrder?.cartOrderStatus = OrderStatus.Placed.orderStatus
                        cartOrder?.updatedAt = System.currentTimeMillis().toString()
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to place all cart orders", false)
        }
    }

    override suspend fun getSelectedCartOrders(): Flow<SelectedCartOrder?> {
        return channelFlow {
            withContext(Dispatchers.IO){
                val selectedCartOrder = realm.query<SelectedCartOrder>("selectedCartId == $0", SELECTED_CART_ORDER_ID).first().asFlow()

                selectedCartOrder.collect { changes ->
                    when (changes) {
                        is InitialObject -> {
                            send(changes.obj)
                        }
                        is UpdatedObject -> {
                            send(changes.obj)
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override suspend fun addSelectedCartOrder(cartOrderId: String): Boolean {
        return try {

            withContext(Dispatchers.IO){
                realm.write {
                    val order = this.query<CartOrder>("cartOrderId == $0", cartOrderId).first().find()
                    val selectedCartOrder = this.query<SelectedCartOrder>("selectedCartId == $0",
                        SELECTED_CART_ORDER_ID
                    ).first().find()

                    if (order != null) {
                        if (selectedCartOrder != null) {
                            if (order.cartOrderId != selectedCartOrder.cartOrder?.cartOrderId){
                                selectedCartOrder.cartOrder = order
                            }
                        }else {
                            this.copyToRealm(SelectedCartOrder(cartOrder = order), UpdatePolicy.ALL)
                        }
                    }
                }
            }

            true
        }catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    override suspend fun deleteSelectedCartOrder(): Boolean {
        return try {
            withContext(Dispatchers.IO){
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
            Resource.Error(e.message ?: "Unable to delete all cart orders", false)
        }
    }
}