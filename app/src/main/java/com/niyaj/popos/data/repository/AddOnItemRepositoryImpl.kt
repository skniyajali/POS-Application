package com.niyaj.popos.data.repository

import com.niyaj.popos.domain.model.AddOnItem
import com.niyaj.popos.domain.repository.AddOnItemRepository
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.add_on_items.AddOnItemRealmDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddOnItemRepositoryImpl(
    private val addOnItemRealmDao: AddOnItemRealmDao
) : AddOnItemRepository {

    override suspend fun getAllAddOnItems(): Flow<Resource<List<AddOnItem>>> {
        return flow {
            addOnItemRealmDao.getAllAddOnItems().collect{ result ->
                when (result){
                    is Resource.Loading -> {
                        emit(Resource.Loading(result.isLoading))
                    }
                    is Resource.Success -> {
                        emit(Resource.Success(
                            result.data?.map { addOnItem ->
                                AddOnItem(
                                    addOnItemId = addOnItem._id,
                                    itemName = addOnItem.itemName,
                                    itemPrice = addOnItem.itemPrice,
                                    created_at = addOnItem.created_at!!,
                                    updated_at = addOnItem.updated_at
                                )
                            }
                        ))
                    }
                    is Resource.Error -> {
                        emit(Resource.Error(result.message ?: "Unable to get items from database"))
                    }
                }
            }
        }
    }

    override suspend fun getAddOnItemById(addOnItemId: String): Resource<AddOnItem?> {
        val result = addOnItemRealmDao.getAddOnItemById(addOnItemId)

        return result.data?.let { addOnItem ->
            Resource.Success(
                data = AddOnItem(
                    addOnItemId = addOnItem._id,
                    itemName = addOnItem.itemName,
                    itemPrice = addOnItem.itemPrice,
                    created_at = addOnItem.created_at!!,
                    updated_at = addOnItem.updated_at
                )
            )
        } ?: Resource.Error(result.message ?: "Could not get add on item")
    }

    override fun findAddOnItemByName(addOnItemName: String, addOnItemId: String?): Boolean {
        return addOnItemRealmDao.findAddOnItemByName(addOnItemName, addOnItemId)
    }

    override suspend fun createNewAddOnItem(newAddOnItem: AddOnItem): Resource<Boolean> {
        return addOnItemRealmDao.createNewAddOnItem(newAddOnItem)
    }

    override suspend fun updateAddOnItem(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return addOnItemRealmDao.updateAddOnItem(newAddOnItem, addOnItemId)
    }

    override suspend fun deleteAddOnItem(addOnItemId: String): Resource<Boolean> {
        return addOnItemRealmDao.deleteAddOnItem(addOnItemId)
    }
}