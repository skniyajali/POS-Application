package com.niyaj.popos.features.addon_item.data.repository

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeAddOnItemRepository: AddOnItemRepository, ValidationRepository {

    private val _addOnItems = mutableListOf<AddOnItem>()

    override suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItem>>> {
        return flow { emit(Resource.Success(_addOnItems)) }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        return Resource.Success(_addOnItems.find { it.addOnItemId == addOnItemId })
    }

    override fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean {
        return _addOnItems.find { it.itemName == addOnItemName} != null
    }

    override suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return Resource.Success(_addOnItems.add(newAddOnItem))
    }

    override suspend fun updateAddOnItem(
        newAddOnItem: AddOnItem,
        addOnItemId: String
    ): Resource<Boolean> {
        val addOnItem = _addOnItems.find { it.addOnItemId == addOnItemId }.also {
            it?.addOnItemId = newAddOnItem.addOnItemId
            it?.itemName = newAddOnItem.itemName
            it?.itemPrice = newAddOnItem.itemPrice
            it?.createdAt = newAddOnItem.createdAt
            it?.updatedAt = newAddOnItem.updatedAt
        }

        return Resource.Success(addOnItem != null)
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return try {
            Resource.Success(_addOnItems.removeIf { it.addOnItemId == addOnItemId })
        }catch (e: Exception) {
            Resource.Error("Unable to delete add on item", false)
        }
    }

    override fun validateItemName(name: String, addOnItemId: String?): ValidationResult {
        if(name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item name must not be empty.",
            )
        }

        if (!name.startsWith("Cold")) {

            val result = name.any { it.isDigit() }

            if(result) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "AddOn item name must not contains any digit.",
                )
            }

            val serverResult = this.findAddOnItemByName(name, addOnItemId)

            if(serverResult){
                return ValidationResult(
                    successful = false,
                    errorMessage = "AddOn item name already exists.",
                )
            }
        }


        return ValidationResult(true)
    }

    override fun validateItemPrice(price: Int): ValidationResult {
        if(price == 0) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item price must not be empty",
            )
        }

        if (price < 5) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item price must greater than 5 rupees.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}