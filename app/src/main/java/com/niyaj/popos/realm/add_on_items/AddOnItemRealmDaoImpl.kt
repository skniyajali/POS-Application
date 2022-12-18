package com.niyaj.popos.realm.add_on_items

import com.niyaj.popos.domain.model.AddOnItem
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

class AddOnItemRealmDaoImpl(
    config: SyncConfiguration
) : AddOnItemRealmDao {

    private val user = realmApp.currentUser


    val realm = Realm.open(config)

    private val sessionState = realm.syncSession.state.name

    init {
        if(user == null && sessionState != "ACTIVE") {
            Timber.d("AddOnItemDao: user is null")
        }

        Timber.d("AddOnItemDao Session: $sessionState")


        CoroutineScope(Dispatchers.IO).launch {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
            realm.subscriptions.waitForSynchronization()
        }
    }

    override suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItemRealm>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<AddOnItemRealm>().sort("_id", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<AddOnItemRealm> ->
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
                send(Resource.Error(e.message ?: "Unable to get AddOnItems", null))
            }
        }

    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItemRealm?> {
        return try {
            val addOnItem = realm.query<AddOnItemRealm>("_id == $0", addOnItemId).first().find()

            Resource.Success(addOnItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get AddOnItem", null)
        }
    }

    override fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean {
        val addOnItem = if(addOnItemId == null) {
            realm.query<AddOnItemRealm>("itemName == $0", addOnItemName).first().find()
        }else {
            realm.query<AddOnItemRealm>("_id != $0 && itemName == $1", addOnItemId, addOnItemName).first().find()
        }

        return addOnItem != null
    }

    override suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        if (user != null){
            return try {
                val addOnItem = AddOnItemRealm(user.id)
                addOnItem.itemName = newAddOnItem.itemName
                addOnItem.itemPrice = newAddOnItem.itemPrice

                val result = realm.write {
                    this.copyToRealm(addOnItem)
                }

                Resource.Success(result.isValid())
            }catch (e: RealmException){
                Resource.Error(e.message ?: "Error creating AddOn Item")
            }
        }else{
            return Resource.Error("User not authenticated", false)
        }
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return try {

            realm.write {
                val addOnItem = this.query<AddOnItemRealm>("_id == $0", addOnItemId).first().find()
                addOnItem?.itemName = newAddOnItem.itemName
                addOnItem?.itemPrice = newAddOnItem.itemPrice
                addOnItem?.updated_at = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item")
        }
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            realm.write {
                val addOnItem: AddOnItemRealm = this.query<AddOnItemRealm>("_id == $0", addOnItemId).find().first()

                delete(addOnItem)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete AddOnItem")
        }
    }
}