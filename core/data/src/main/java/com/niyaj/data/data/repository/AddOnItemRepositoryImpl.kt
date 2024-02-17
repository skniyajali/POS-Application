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
import com.niyaj.data.mapper.toEntity
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
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class AddOnItemRepositoryImpl(
    config: RealmConfiguration,
    private val ioDispatcher: CoroutineDispatcher,
) : AddOnItemRepository, AddOnItemValidationRepository {

    val realm = Realm.open(config)

    override suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>> {
        return withContext(ioDispatcher) {
            realm.query<AddOnItemEntity>()
                .sort("addOnItemId", Sort.DESCENDING)
                .find()
                .asFlow()
                .mapLatest { data ->
                    data.collectWithSearch(
                        transform = { it.toExternalModel() },
                        searchFilter = { it.searchAddOnItem(searchText) },
                    )
                }
        }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        return withContext(ioDispatcher) {
            try {
                val addOnItem =
                    realm.query<AddOnItemEntity>("addOnItemId == $0", addOnItemId).first().find()

                Resource.Success(addOnItem?.toExternalModel())
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Unable to get AddOnItem")
            }
        }
    }

    override suspend fun findAddOnItemByName(itemName: String, itemId: String?): Boolean {
        return withContext(ioDispatcher) {
            if (itemId.isNullOrEmpty()) {
                realm.query<AddOnItemEntity>("itemName == $0", itemName).first().find()
            } else {
                realm.query<AddOnItemEntity>(
                    "addOnItemId != $0 && itemName == $1",
                    itemId,
                    itemName
                ).first().find()
            } != null
        }
    }

    override suspend fun createOrUpdateItem(newItem: AddOnItem, itemId: String): Resource<Boolean> {
        return withContext(ioDispatcher) {
            try {
                val validateName = validateItemName(newItem.itemName, itemId)
                val validatePrice = validateItemPrice(newItem.itemPrice)

                val hasError = listOf(validateName, validatePrice).any { !it.successful }

                if (!hasError) {
                    val addOnItem = realm
                        .query<AddOnItemEntity>("addOnItemId == $0", itemId)
                        .first()
                        .find()

                    if (addOnItem != null) {
                        realm.write {
                            findLatest(addOnItem)?.apply {
                                this.itemName = newItem.itemName
                                this.itemPrice = newItem.itemPrice
                                this.isApplicable = newItem.isApplicable
                                this.updatedAt = System.currentTimeMillis().toString()
                            }
                        }

                        Resource.Success(true)
                    } else {
                        realm.write {
                            this.copyToRealm(newItem.toEntity())
                        }

                        Resource.Success(true)
                    }
                } else {
                    Resource.Error("Unable to update item")
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Failed to update item")
            }
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

    override suspend fun validateItemName(name: String, addOnItemId: String?): ValidationResult {
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

            val serverResult = withContext(ioDispatcher) {
                findAddOnItemByName(name, addOnItemId)
            }

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