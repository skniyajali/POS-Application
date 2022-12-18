package com.niyaj.popos.domain.repository

import com.niyaj.popos.domain.model.AddOnItem
import com.niyaj.popos.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface AddOnItemRepository {

    suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItem>>>

    suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?>

    fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean

    suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean>

    suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean>

    suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean>
}