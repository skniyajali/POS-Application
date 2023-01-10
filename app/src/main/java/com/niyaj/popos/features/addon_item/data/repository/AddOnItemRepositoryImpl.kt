package com.niyaj.popos.features.addon_item.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_DIGIT_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_NAME_LENGTH_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.popos.features.addon_item.domain.util.AddOnConstants.ADDON_WHITELIST_ITEM
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.exceptions.RealmException
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AddOnItemRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AddOnItemRepository, ValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("realm file ${config.path}")
        Timber.d("AddOnItemDao Session")
    }

    override suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItem>>> {
        return channelFlow {
            withContext(ioDispatcher) {
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
                    send(Resource.Loading(false))
                    send(Resource.Error(message = e.message ?: "Unable to get AddOnItems", data = emptyList()))
                }
            }
        }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        return try {
            val addOnItem = withContext(ioDispatcher) {
                realm.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()
            }

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
            withContext(ioDispatcher){
                val addOnItem = AddOnItem()
                addOnItem.addOnItemId = BsonObjectId().toHexString()
                addOnItem.itemName = newAddOnItem.itemName
                addOnItem.itemPrice = newAddOnItem.itemPrice
                addOnItem.createdAt = System.currentTimeMillis().toString()

                realm.write {
                    this.copyToRealm(addOnItem)
                }
            }

            Resource.Success(true)
        }catch (e: RealmException){
            Timber.e(e)
            Resource.Error(e.message ?: "Error creating AddOn Item", false)
        }
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val addOnItem = this.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()
                    addOnItem?.itemName = newAddOnItem.itemName
                    addOnItem?.itemPrice = newAddOnItem.itemPrice
                    addOnItem?.updatedAt = System.currentTimeMillis().toString()
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item", false)
        }
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            withContext(ioDispatcher){
                realm.write {
                    val addOnItem: AddOnItem = this.query<AddOnItem>("addOnItemId == $0", addOnItemId).find().first()

                    delete(addOnItem)
                }
            }

            Resource.Success(true)

        } catch (e: Exception){
            Resource.Error(e.message ?: "Failed to delete AddOnItem", false)
        }
    }

    override fun validateItemName(name: String, addOnItemId: String?): ValidationResult {
        if(name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_NAME_EMPTY_ERROR,
            )
        }

        if (name.length < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_NAME_LENGTH_ERROR,
            )
        }

        if (!name.startsWith(ADDON_WHITELIST_ITEM)) {

            val result = name.any { it.isDigit() }

            if(result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDON_NAME_DIGIT_ERROR,
                )
            }

            val serverResult = this.findAddOnItemByName(name, addOnItemId)

            if(serverResult){
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDON_NAME_ALREADY_EXIST_ERROR,
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }

    override fun validateItemPrice(price: Int): ValidationResult {
        if(price == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_PRICE_EMPTY_ERROR,
            )
        }

        if (price < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = ADDON_PRICE_LESS_THAN_FIVE_ERROR,
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}