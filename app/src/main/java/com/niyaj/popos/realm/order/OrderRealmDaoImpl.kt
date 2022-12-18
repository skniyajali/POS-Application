package com.niyaj.popos.realm.order

import com.niyaj.popos.domain.util.OrderStatus
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realmApp
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class OrderRealmDaoImpl(
    config: SyncConfiguration
) : OrderRealmDao {

    private val user = realmApp.currentUser

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if(user == null && sessionState != "ACTIVE") {
            Timber.d("OrderRealmDaoImpl: user is null")
        }

        Timber.d("Order Session: $sessionState")

        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.downloadAllServerChanges()
            realm.syncSession.uploadAllLocalChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }

    override suspend fun getAllOrders(startDate: String, endDate: String): Flow<Resource<List<CartRealm>>> {
        return channelFlow {
            if(user != null){
                try {
                    send(Resource.Loading(true))

                    val items = realm.query<CartRealm>(
                        "cartOrder.cartOrderStatus != $0 AND cartOrder.updated_at >= $1 AND cartOrder.updated_at <= $2",
                        OrderStatus.Processing.orderStatus,
                        startDate,
                        endDate,
                    ).sort("_id", Sort.DESCENDING).find()

                    val itemFlow = items.asFlow()
                    itemFlow.collect{ changes: ResultsChange<CartRealm> ->
                        when (changes){
                            is InitialResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                            is UpdatedResults -> {
                                send(Resource.Success(changes.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }catch (e: Exception){
                    send(Resource.Error(e.message ?: "Unable to get order", emptyList()))
                }
            } else {
                send(Resource.Error("User is not authenticated", emptyList()))
            }
        }
    }

    override suspend fun getOrderDetails(cartOrderId: String): Resource<List<CartRealm>> {
        return if (user != null){
            try {
                val cartOrder = realm.query<CartRealm>("cartOrder._id == $0", cartOrderId).find()

                if (cartOrder.isNotEmpty()){
                    Resource.Success(cartOrder)
                }else{
                    Resource.Success(emptyList())
                }
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to find cart order", emptyList())
            }

        } else {
            Resource.Error("User is not authenticated", emptyList())
        }
    }

    override suspend fun updateOrderStatus(cartOrderId: String, orderStatus: String): Resource<Boolean> {
        return if(user != null) {
            try {
                val cartOrder = realm.query<CartOrderRealm>("_id == $0", cartOrderId).first().find()

                val newOrderStatus =  if(orderStatus == OrderStatus.Delivered.orderStatus){
                    if (cartOrder?.cartOrderStatus  == orderStatus){
                        OrderStatus.Placed.orderStatus
                    }else{
                        OrderStatus.Delivered.orderStatus
                    }
                }else {
                    orderStatus
                }

                if (cartOrder != null){
                    realm.writeBlocking {
                        findLatest(cartOrder).also {
                            it?.cartOrderStatus = newOrderStatus
                            it?.updated_at = System.currentTimeMillis().toString()
                        }
                    }

                    Resource.Success(true)
                }else{
                    Resource.Error("Unable to find order", false)
                }
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to update order status", false)
            }
        } else {
            Resource.Error("User is not authorized", false)
        }
    }

    override suspend fun deleteOrder(cartOrderId: String): Resource<Boolean> {
        return if(user != null) {
              try {
                  realm.write {
                      val cartOrder = this.query<CartOrderRealm>("_id == $0", cartOrderId).first().find()
                      if(cartOrder != null){
                          val cartProducts: RealmResults<CartRealm> = this.query<CartRealm>("cartOrder._id == $0", cartOrderId).find()

                          delete(cartProducts)
                          delete(cartOrder)
                      }
                  }

                  Resource.Success(true)
              }catch (e: Exception){
                  Timber.e(e)
                  Resource.Error(e.message ?: "Unable to delete order", false)
              }
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

}