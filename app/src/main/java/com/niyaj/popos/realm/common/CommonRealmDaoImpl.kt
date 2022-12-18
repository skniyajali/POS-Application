package com.niyaj.popos.realm.common

import com.niyaj.popos.domain.util.CartOrderType
import com.niyaj.popos.realm.cart.CartRealm
import com.niyaj.popos.realm.cart_order.CartOrderRealm
import com.niyaj.popos.realm.charges.ChargesRealm
import com.niyaj.popos.realmApp
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class CommonRealmDaoImpl(
    config: SyncConfiguration
) : CommonRealmDao {

    private val user = realmApp.currentUser

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if(user == null && sessionState != "ACTIVE") {
            Timber.d("Common Realm: user is null")
        }

        Timber.d("Common Session: $sessionState")

        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.downloadAllServerChanges()
            realm.syncSession.uploadAllLocalChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }

    override fun countTotalPrice(cartOrderId: String): Pair<Int, Int> {
        var totalPrice = 0
        var discountPrice = 0

        val cartOrder = realm.query<CartOrderRealm>("_id == $0", cartOrderId).find().first()
        val cartOrders =  realm.query<CartRealm>("cartOrder._id == $0", cartOrderId).find()

        if(cartOrder.doesChargesIncluded){
            val charges = realm.query<ChargesRealm>().find()
            for(charge in charges){
                if(charge.isApplicable && cartOrder.orderType != CartOrderType.DineIn.orderType){
                    totalPrice += charge.chargesPrice
                }
            }
        }

        if (cartOrder.addOnItems.isNotEmpty()){
            for (addOnItem in cartOrder.addOnItems){

                totalPrice += addOnItem.itemPrice

                // Todo: use dynamic fields for discount calculation.
                if(addOnItem.itemName == "Masala" || addOnItem.itemName == "Mayonnaise"){
                    discountPrice += addOnItem.itemPrice
                }
            }
        }

        for (cartOrder1 in cartOrders) {
            if(cartOrder1.product != null){
                totalPrice += cartOrder1.quantity.times(cartOrder1.product?.productPrice!!)
            }
        }

        return Pair(totalPrice, discountPrice)
    }

}