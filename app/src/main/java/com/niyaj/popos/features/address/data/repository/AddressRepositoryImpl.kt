package com.niyaj.popos.features.address.data.repository

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.features.address.domain.repository.AddressRepository
import com.niyaj.popos.features.common.util.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AddressRepositoryImpl(
    config: RealmConfiguration
) : AddressRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("Address Session:")
    }

    override suspend fun getAllAddress(): Flow<Resource<List<Address>>> {
        return channelFlow {
            withContext(Dispatchers.IO) {
                try {
                    send(Resource.Loading(true))
                    val items = realm.query<Address>().sort("addressId", Sort.DESCENDING).find()

                    val itemsFlow = items.asFlow()
                    itemsFlow.collect { changes: ResultsChange<Address> ->
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
                } catch (e: Exception){
                    send(Resource.Loading(false))
                    send(Resource.Error(e.message ?: "Unable to get addresses", emptyList()))
                }
            }
        }
    }

    override suspend fun getAddressById(addressId: String): Resource<Address?> {
        return try {
            val address = realm.query<Address>("addressId == $0", addressId).first().find()
            Resource.Success(address)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get address", null)
        }
    }

    override suspend fun addNewAddress(newAddress: Address): Resource<Boolean> {
        return try {
            withContext(Dispatchers.IO){
                val address = Address()
                address.addressId = BsonObjectId().toHexString()
                address.shortName = newAddress.shortName
                address.addressName = newAddress.addressName
                address.createdAt = System.currentTimeMillis().toString()

                realm.write {
                    this.copyToRealm(address)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to new address", false)
        }
    }

    override suspend fun updateAddress(newAddress: Address, addressId: String): Resource<Boolean> {
        return try {
            withContext(Dispatchers.IO){
                realm.write {
                    val address = this.query<Address>("addressId == $0", addressId).first().find()
                    address?.shortName = newAddress.shortName
                    address?.addressName = newAddress.addressName
                    address?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to update address", false)
        }
    }

    override suspend fun deleteAddress(addressId: String): Resource<Boolean> {
        return try {
            withContext(Dispatchers.IO){
                realm.write {
                    val address: Address =
                        this.query<Address>("addressId == $0", addressId).find().first()

                    delete(address)
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to delete address", false)
        }
    }

}