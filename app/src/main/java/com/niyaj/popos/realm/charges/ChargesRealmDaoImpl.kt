package com.niyaj.popos.realm.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.util.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import timber.log.Timber

class ChargesRealmDaoImpl(
    config: RealmConfiguration
) : ChargesRealmDao {

    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        Timber.d("ChargesDaoImpl Session: $sessionState")
    }

    override suspend fun getAllCharges(): Flow<Resource<List<ChargesRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<ChargesRealm>().sort("_id", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<ChargesRealm> ->
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

    override suspend fun getChargesById(chargesId: String): Resource<ChargesRealm?> {
        return try {
            val chargesItem = realm.query<ChargesRealm>("_id == $0", chargesId).first().find()

            Resource.Success(chargesItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get Charges", null)
        }
    }

    override fun findChargesByName(chargesName: String, chargesId: String?): Boolean {
        val charges = if (chargesId == null) {
            realm.query<ChargesRealm>("chargesName == $0", chargesName).first().find()
        } else {
            realm.query<ChargesRealm>("_id != $0 && chargesName == $1", chargesId, chargesName)
                .first().find()
        }

        return charges != null
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        return try {
            val chargesItem = ChargesRealm()
            chargesItem.chargesName = newCharges.chargesName
            chargesItem.chargesPrice = newCharges.chargesPrice
            chargesItem.isApplicable = newCharges.isApplicable

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
                val chargesItem = this.query<ChargesRealm>("_id == $0", chargesId).first().find()
                chargesItem?.chargesName = newCharges.chargesName
                chargesItem?.chargesPrice = newCharges.chargesPrice
                chargesItem?.isApplicable = newCharges.isApplicable
                chargesItem?.updated_at = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update charges item")
        }
    }

    override suspend fun deleteCharges(chargesId: String): Resource<Boolean> {
        return try {
            realm.write {
                val chargesItem: ChargesRealm =
                    this.query<ChargesRealm>("_id == $0", chargesId).find().first()

                delete(chargesItem)
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete charges item")
        }
    }

}