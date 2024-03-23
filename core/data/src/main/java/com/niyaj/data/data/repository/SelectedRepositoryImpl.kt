package com.niyaj.data.data.repository

import com.niyaj.common.utils.Constants
import com.niyaj.data.repository.SelectedRepository
import com.niyaj.data.utils.collectAndSend
import com.niyaj.database.model.CartOrderEntity
import com.niyaj.database.model.SelectedCartOrderEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.CartOrder
import com.niyaj.model.OrderStatus
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext

class SelectedRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : SelectedRepository {

    private val realm = Realm.open(config)

    override fun getAllProcessingCartOrder(): Flow<List<CartOrder>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val data = realm
                        .query<CartOrderEntity>(
                            "cartOrderStatus == $0",
                            OrderStatus.PROCESSING.name
                        ).find().asFlow()

                    data.collectAndSend(
                        transform = { it.toExternalModel()},
                        send = {
                            send(it)
                        }
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override fun getSelectedCartOrders(): Flow<String?> {
        return channelFlow {
            withContext(ioDispatcher){
                val selectedCartOrder = realm
                    .query<SelectedCartOrderEntity>("selectedCartId == $0",
                        Constants.SELECTED_CART_ORDER_ID
                    )
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

    override suspend fun markOrderAsSelected(cartOrderId: String): Boolean {
        return try {
            withContext(ioDispatcher){
                val order = realm.query<CartOrderEntity>("cartOrderId == $0", cartOrderId).first().find()

                if (order != null && order.cartOrderStatus != OrderStatus.PLACED.name) {
                    realm.write {
                        val selectedCartOrder = this.query<SelectedCartOrderEntity>("selectedCartId == $0",
                            Constants.SELECTED_CART_ORDER_ID
                        ).first().find()

                        if (selectedCartOrder != null) {
                            findLatest(order)?.let {
                                selectedCartOrder.cartOrder = it
                            }
                        }else {
                            val cartOrder = SelectedCartOrderEntity()
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
            false
        }
    }
}