package com.niyaj.popos.realm.charges

import com.niyaj.popos.domain.model.Charges
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realmApp
import io.realm.kotlin.Realm
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

class ChargesRealmDaoImpl(
    config: SyncConfiguration
) : ChargesRealmDao {


    private val user = realmApp.currentUser


    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if(user == null && sessionState != "ACTIVE") {
            Timber.d("ChargesDaoImpl: user is null")
        }

        Timber.d("ChargesDaoImpl Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
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
            }catch (e: Exception){
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
        val charges = if(chargesId == null) {
            realm.query<ChargesRealm>("chargesName == $0", chargesName).first().find()
        }else {
            realm.query<ChargesRealm>("_id != $0 && chargesName == $1", chargesId, chargesName).first().find()
        }

        return charges != null
    }

    override suspend fun createNewCharges(newCharges: Charges): Resource<Boolean> {
        if (user != null){
            return try {
                val chargesItem = ChargesRealm(user.id)
                chargesItem.chargesName = newCharges.chargesName
                chargesItem.chargesPrice = newCharges.chargesPrice
                chargesItem.isApplicable = newCharges.isApplicable

                val result = realm.write {
                    this.copyToRealm(chargesItem)
                }

                Resource.Success(result.isValid())
            }catch (e: RealmException){
                Resource.Error(e.message ?: "Error creating Charges Item")
            }
        }else{
            return Resource.Error("User not authenticated", false)
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
                val chargesItem: ChargesRealm = this.query<ChargesRealm>("_id == $0", chargesId).find().first()

                delete(chargesItem)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete charges item")
        }
    }

}