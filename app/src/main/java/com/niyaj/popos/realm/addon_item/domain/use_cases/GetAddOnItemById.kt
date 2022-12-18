package com.niyaj.popos.realm.addon_item.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository

class GetAddOnItemById(
    private val addOnItemRepository: AddOnItemRepository
) {

    suspend operator fun invoke(addOnItemId: String): Resource<AddOnItem?>{
        return addOnItemRepository.getAddOnItemById(addOnItemId)
    }
}