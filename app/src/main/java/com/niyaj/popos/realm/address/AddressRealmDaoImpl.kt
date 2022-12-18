package com.niyaj.popos.realm.address

import com.niyaj.popos.domain.model.Address
import com.niyaj.popos.domain.util.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

class AddressRealmDaoImpl(
    config: RealmConfiguration
) : AddressRealmDao {

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        Timber.d("Address Session: $sessionState")
    }

    override suspend fun getAllAddress(): Flow<Resource<List<AddressRealm>>> {
        return channelFlow {
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
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get addresses"))
            }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<AddressRealm?> {
        return try {
            val address = realm.query<AddressRealm>("_id == $0", addressId).first().find()
            Resource.Success(address)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get address", null)
        }
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return try {
            val address = AddressRealm()
            address.shortName = newAddress.shortName
            address.addressName = newAddress.addressName

            realm.write {
                this.copyToRealm(address)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address", false)
        }
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        return try {
            realm.write {
                val address = this.query<AddressRealm>("_id == $0", addressId).first().find()
                address?.shortName = newAddress.shortName
                address?.addressName = newAddress.addressName
                address?.updated_at = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update address", false)
        }
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return try {
            realm.write {
                val address: AddressRealm =
                    this.query<AddressRealm>("_id == $0", addressId).find().first()

                delete(address)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete address", false)
        }
    }
}