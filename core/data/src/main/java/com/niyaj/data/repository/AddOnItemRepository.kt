package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.AddOnItem
import kotlinx.coroutines.flow.Flow

interface AddOnItemRepository {

    suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>>

    suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?>

    suspend fun findAddOnItemByName(itemName: String, itemId: String?): Boolean

    suspend fun createOrUpdateItem(newItem: AddOnItem, itemId: String): Resource<Boolean>

    suspend fun deleteAddOnItems(addOnItemIds: List<String>): Resource<Boolean>
}