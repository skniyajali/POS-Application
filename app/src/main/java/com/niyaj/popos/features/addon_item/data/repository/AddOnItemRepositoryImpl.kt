package com.niyaj.popos.features.addon_item.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.common.util.Resource
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

class AddOnItemRepositoryImpl(
    config: RealmConfiguration
) : AddOnItemRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("realm file ${config.path}")
        Timber.d("AddOnItemDao Session")
    }

    override suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItem>>> {
        return channelFlow {
            try {
                send(Resource.Loading(true))

                val items = realm.query<AddOnItem>().sort("addOnItemId", Sort.DESCENDING).find().asFlow()

                items.collect { changes: ResultsChange<AddOnItem> ->
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
                send(Resource.Error(e.message ?: "Unable to get AddOnItems"))
            }
        }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        return try {
            val addOnItem = realm.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()

            Resource.Success(addOnItem)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get AddOnItem", null)
        }
    }

    override fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean {
        val addOnItem = if(addOnItemId == null) {
            realm.query<AddOnItem>("itemName == $0", addOnItemName).first().find()
        }else {
            realm.query<AddOnItem>("addOnItemId != $0 && itemName == $1", addOnItemId, addOnItemName).first().find()
        }

        return addOnItem != null
    }

    override suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            val addOnItem = AddOnItem()
            addOnItem.addOnItemId = BsonObjectId().toHexString()
            addOnItem.itemName = newAddOnItem.itemName
            addOnItem.itemPrice = newAddOnItem.itemPrice
            addOnItem.createdAt = System.currentTimeMillis().toString()

            val result = realm.write {
                this.copyToRealm(addOnItem)
            }

            Resource.Success(result.isValid())
        }catch (e: RealmException){
            Timber.e(e)
            Resource.Error(e.message ?: "Error creating AddOn Item")
        }
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return try {
            realm.write {
                val addOnItem = this.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()
                addOnItem?.itemName = newAddOnItem.itemName
                addOnItem?.itemPrice = newAddOnItem.itemPrice
                addOnItem?.updatedAt = System.currentTimeMillis().toString()
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item")
        }
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            realm.write {
                val addOnItem: AddOnItem = this.query<AddOnItem>("addOnItemId == $0", addOnItemId).find().first()

                delete(addOnItem)
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete AddOnItem")
        }
    }
}