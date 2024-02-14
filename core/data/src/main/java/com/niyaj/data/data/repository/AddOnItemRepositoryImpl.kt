package com.niyaj.data.data.repository

import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_ALREADY_EXIST_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_DIGIT_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_EMPTY_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_NAME_LENGTH_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_PRICE_EMPTY_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_PRICE_LESS_THAN_FIVE_ERROR
import com.niyaj.common.tags.AddOnConstants.ADDON_WHITELIST_ITEM
import com.niyaj.common.utils.Resource
import com.niyaj.common.utils.ValidationResult
import com.niyaj.data.repository.AddOnItemRepository
import com.niyaj.data.repository.validation.AddOnItemValidationRepository
import com.niyaj.data.utils.collectWithSearch
import com.niyaj.database.model.AddOnItemEntity
import com.niyaj.database.model.toExternalModel
import com.niyaj.model.AddOnItem
import com.niyaj.model.searchAddOnItem
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import timber.log.Timber

class AddOnItemRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : AddOnItemRepository, AddOnItemValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>> {
        return channelFlow {
            withContext(ioDispatcher) {
                try {
                    val data = realm.query<AddOnItemEntity>()
                        .sort("addOnItemId", Sort.DESCENDING)
                        .find()
                        .asFlow()

                    data.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.searchAddOnItem(searchText) },
                        send = { send(it) },
                    )
                } catch (e: Exception) {
                    send(emptyList())
                }
            }
        }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        return try {
            val addOnItem =
                realm.query<AddOnItemEntity>("addOnItemId == $0", addOnItemId).first().find()

            Resource.Success(addOnItem?.toExternalModel())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unable to get AddOnItem")
        }
    }

    override fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean {
        val addOnItem = if (addOnItemId.isNullOrEmpty()) {
            realm.query<AddOnItemEntity>("itemName == $0", addOnItemName).first().find()
        } else {
            realm.query<AddOnItemEntity>(
                "addOnItemId != $0 && itemName == $1",
                addOnItemId,
                addOnItemName
            ).first().find()
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
                    val addOnItem = AddOnItemEntity()
                    addOnItem.addOnItemId =
                        newAddOnItem.addOnItemId.ifEmpty { BsonObjectId().toHexString() }
                    addOnItem.itemName = newAddOnItem.itemName
                    addOnItem.itemPrice = newAddOnItem.itemPrice
                    addOnItem.isApplicable = newAddOnItem.isApplicable
                    addOnItem.createdAt =
                        newAddOnItem.createdAt.ifEmpty { System.currentTimeMillis().toString() }

                    realm.write {
                        this.copyToRealm(addOnItem)
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to create new addon item")
            }
        } catch (e: Exception) {
            Timber.e(e)
            Resource.Error(e.message ?: "Error creating AddOn Item")
        }
    }

    override suspend fun updateAddOnItem(
        newAddOnItem: AddOnItem,
        addOnItemId: String
    ): Resource<Boolean> {
        return try {
            val validateName = validateItemName(newAddOnItem.itemName, addOnItemId)
            val validatePrice = validateItemPrice(newAddOnItem.itemPrice)

            val hasError = listOf(validateName, validatePrice).any { !it.successful }

            if (!hasError) {
                val addOnItem =
                    realm.query<AddOnItemEntity>("addOnItemId == $0", addOnItemId).first().find()

                if (addOnItem != null) {
                    withContext(ioDispatcher) {
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
                } else {
                    Resource.Error("Unable to find add on item")
                }
            } else {
                Resource.Error("Unable to update item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item")
        }
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            val addOnItem =
                realm.query<AddOnItemEntity>("addOnItemId == $0", addOnItemId).first().find()

            if (addOnItem != null) {
                withContext(ioDispatcher) {
                    realm.write {
                        findLatest(addOnItem)?.let {
                            delete(it)
                        }
                    }
                }

                Resource.Success(true)
            } else {
                Resource.Error("Unable to find add on item")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete AddOnItem")
        }
    }

    override suspend fun deleteAddOnItems(addOnItemIds: List<String>): Resource<Boolean> {
        return try {
            withContext(ioDispatcher) {
                addOnItemIds.forEach { addOnId ->
                    val addOnItem = realm.query<AddOnItemEntity>("addOnItemId == $0", addOnId)
                        .first()
                        .find()

                    addOnItem?.let {
                        realm.write {
                            findLatest(it)?.let {
                                delete(it)
                            }
                        }
                    }
                }
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete AddOnItem")
        }
    }

    override fun validateItemName(name: String, addOnItemId: String?): ValidationResult {
        if (name.isEmpty()) {
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

            if (result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = ADDON_NAME_DIGIT_ERROR,
                )
            }

            val serverResult = this.findAddOnItemByName(name, addOnItemId)

            if (serverResult) {
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
        if (price == 0) {
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