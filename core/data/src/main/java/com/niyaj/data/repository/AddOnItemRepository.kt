package com.niyaj.data.repository

import com.niyaj.common.utils.Resource
import com.niyaj.model.AddOnItem
import kotlinx.coroutines.flow.Flow

interface AddOnItemRepository {

    suspend fun getAllAddOnItems(searchText: String): Flow<List<AddOnItem>>

    suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?>

    fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean

    suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean>

    suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean>

    suspend fun deleteAddOnItems(addOnItemIds: List<String>): Resource<Boolean>
}