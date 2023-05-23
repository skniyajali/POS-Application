package com.niyaj.popos.features.addon_item.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemValidationRepository
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
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AddOnItemRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AddOnItemRepository, AddOnItemValidationRepository {

    val realm = Realm.open(config)

    init {
        Timber.d("realm file ${config.path}")
        Timber.d("AddOnItemDao Session")
    }

    override suspend fun getAllAddOnItems() : Flow<Resource<List<AddOnItem>>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    send(Resource.Loading(true))

                    val data = realm.query<AddOnItem>().sort("addOnItemId", Sort.DESCENDING).find().asFlow()

                    data.collectLatest { result ->
                        when (result) {
                            is InitialResults -> {
                                send(Resource.Success(result.list))
                                send(Resource.Loading(false))
                            }
                            is UpdatedResults -> {
                                send(Resource.Success(result.list))
                                send(Resource.Loading(false))
                            }
                        }
                    }
                }catch (e: Exception) {
                    send(Resource.Error(e.message ?: "Unable to get all add on items"))
                }
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
        val addOnItem = if(addOnItemId.isNullOrEmpty()) {
            realm.query<AddOnItem>("itemName == $0", addOnItemName).first().find()
        }else {
            realm.query<AddOnItem>("addOnItemId != $0 && itemName == $1", addOnItemId, addOnItemName).first().find()
        }

        return addOnItem != null
    }

    override suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return try {
            val validateName = validateItemName(newAddOnItem.itemName, null)
            val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

            val hasError = listOf(validateName, validatePrice).any { !it.successful }

            if (!hasError) {
                withContext(ioDispatcher) {
                    val addOnItem = AddOnItem()
                    addOnItem.addOnItemId = newAddOnItem.addOnItemId.ifEmpty { BsonObjectId().toHexString() }
                    addOnItem.itemName = newAddOnItem.itemName
                    addOnItem.itemPrice = newAddOnItem.itemPrice
                    addOnItem.isApplicable = newAddOnItem.isApplicable
                    addOnItem.createdAt = newAddOnItem.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(addOnItem)
                    }
                }

                Resource.Success(true)
            }else {
                Resource.Error( "Unable to create new addon item", false)
            }
        }catch (e: Exception){
            Timber.e(e)
            Resource.Error(e.message ?: "Error creating AddOn Item", false)
        }
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return try {
            val validateName = validateItemName(newAddOnItem.itemName, addOnItemId)
            val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

            val hasError = listOf(validateName, validatePrice).any { !it.successful }

            if (!hasError){
                val addOnItem = realm.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()

                if (addOnItem != null) {
                    withContext(ioDispatcher){
                        realm.write {
                            findLatest(addOnItem)?.apply {
                                this.itemName = newAddOnItem.itemName
                                this.itemPrice = newAddOnItem.itemPrice
                                this.isApplicable = newAddOnItem.isApplicable
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }
                    }

                    Resource.Success(true)
                }else {
                    Resource.Error("Unable to find add on item", false)
                }
            }else {
                Resource.Error( "Unable to update item", false)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item", false)
        }
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            val addOnItem = realm.query<AddOnItem>("addOnItemId == $0", addOnItemId).first().find()

            if (addOnItem != null){
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(addOnItem)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            }else{
                Resource.Error("Unable to find add on item", false)
            }
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