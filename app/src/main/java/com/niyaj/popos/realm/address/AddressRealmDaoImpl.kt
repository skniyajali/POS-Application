package com.niyaj.popos.realm.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realmApp
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressRealmDaoImpl(
    config: SyncConfiguration
) : AddressRealmDao {

    private val user = realmApp.currentUser
    
    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if (user == null && sessionState != "ACTIVE") {
            Timber.d("AddressRealmDaoImpl: user is null")
        }

        Timber.d("Address Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }

    override suspend fun getAllAddress(): Flow<Resource<List<AddressRealm>>> {
        return channelFlow {
            if (user != null) {
                try {
                    send(Resource.Loading(true))
                    val items = realm.query<AddressRealm>().sort("_id", Sort.DESCENDING).find()

                    val itemsFlow = items.asFlow()
                    itemsFlow.collect { changes: ResultsChange<AddressRealm> ->
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
                }catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get addresses"))
                }

            }else {
                send(Resource.Error("User is not authenticated", listOf()))
            }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<AddressRealm?> {
        return if (user != null) {
            try {
                val address =  realm.query<AddressRealm>("_id == $0", addressId).first().find()
                Resource.Success(address)
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to get address", null)
            }
        } else {
            Resource.Error("User is not authenticated", null)
        }
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return if (user != null) {
            try {
                val address = AddressRealm(user.id)
                address.shortName = newAddress.shortName
                address.addressName = newAddress.addressName

                realm.write {
                    this.copyToRealm(address)
                }

                Resource.Success(true)
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to new address", false)
            }
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        return if (user != null) {
            try {
                realm.write {
                    val address = this.query<AddressRealm>("_id == $0", addressId).first().find()
                    address?.shortName = newAddress.shortName
                    address?.addressName = newAddress.addressName
                    address?.updated_at = System.currentTimeMillis().toString()
                }

                Resource.Success(true)
            }catch (e: Exception){
               Resource.Error(e.message ?: "Unable to update address", false)
            }
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return if (user != null) {
            try {
                realm.write {
                    val address: AddressRealm =
                        this.query<AddressRealm>("_id == $0", addressId).find().first()

                    delete(address)
                }

                Resource.Success(true)
            }catch (e: Exception){
                Resource.Error(e.message ?: "Unable to delete address", false)
            }
        } else {
            Resource.Error("User is not authenticated", false)
        }
    }

}