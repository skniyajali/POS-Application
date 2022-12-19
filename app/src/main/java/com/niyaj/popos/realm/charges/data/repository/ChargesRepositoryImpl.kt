package com.niyaj.popos.realm.charges.data.repository

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.charges.domain.model.Charges
import com.niyaj.popos.realm.charges.domain.repository.ChargesRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class ChargesRepositoryImpl(
    config: RealmConfiguration
) : ChargesRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("ChargesDaoImpl Session")
    }

    override suspend fun getAllCharges(): Flow<Resource<List<com.niyaj.popos.realm.charges.domain.model.Charges>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<com.niyaj.popos.realm.charges.domain.model.Charges>().sort("chargesId", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<com.niyaj.popos.realm.charges.domain.model.Charges> ->
                    when (changes) {
                        is UpdatedResults -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }

                        is InitialResults -> {
                            send(Resource.Success(changes.list))
                            send(Resource.Loading(false))
                        }
                    }
                }
            } catch (e: Exception) {
                send(Resource.Error(e.message ?: "Unable to get charges items", null))
            }
        }
    }

    override suspend fun getChargesById(chargesId: String): Resource<com.niyaj.popos.realm.charges.domain.model.Charges?> {
        return try {
            val chargesItem = realm.query<com.niyaj.popos.realm.charges.domain.model.Charges>("chargesId == $0", chargesId).first().find()

            Resource.Success(chargesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Charges", null)
        }
    }

    override fun findChargesByName(chargesName: String, chargesId: String?): Boolean {
        val charges = if (chargesId == null) {
            realm.query<com.niyaj.popos.realm.charges.domain.model.Charges>("chargesName == $0", chargesName).first().find()
        } else {
            realm.query<com.niyaj.popos.realm.charges.domain.model.Charges>("chargesId != $0 && chargesName == $1", chargesId, chargesName)
                .first().find()
        }

        return charges != null
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        return try {
            val chargesItem = com.niyaj.popos.realm.charges.domain.model.Charges()
            chargesItem.chargesId = BsonObjectId().toHexString()
            chargesItem.chargesName = newCharges.chargesName
            chargesItem.chargesPrice = newCharges.chargesPrice
            chargesItem.isApplicable = newCharges.isApplicable
            chargesItem.createdAt = System.currentTimeMillis().toString()

            val result = realm.write {
                this.copyToRealm(chargesItem)
            }

            Resource.Success(result.isValid())
        } catch (e: RealmException) {
            Resource.Error(e.message ?: "Error creating Charges Item")
        }
    }

    override suspend fun updateCharges(
        newCharges: Charges,
        chargesId: String,
    ): Resource<Boolean> {
        return try {

            realm.write {
                val chargesItem = this.query<com.niyaj.popos.realm.charges.domain.model.Charges>("chargesId == $0", chargesId).first().find()
                chargesItem?.chargesName = newCharges.chargesName
                chargesItem?.chargesPrice = newCharges.chargesPrice
                chargesItem?.isApplicable = newCharges.isApplicable
                chargesItem?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update charges item")
        }
    }

    override suspend fun deleteCharges(chargesId: String): Resource<Boolean> {
        return try {
            realm.write {
                val chargesItem: com.niyaj.popos.realm.charges.domain.model.Charges =
                    this.query<com.niyaj.popos.realm.charges.domain.model.Charges>("chargesId == $0", chargesId).find().first()

                delete(chargesItem)
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete charges item")
        }
    }

}