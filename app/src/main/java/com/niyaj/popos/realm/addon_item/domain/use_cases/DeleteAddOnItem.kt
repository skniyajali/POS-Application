package com.niyaj.popos.realm.addon_item.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository

class DeleteAddOnItem(
    private val addOnItemRepository: AddOnItemRepository
) {
    suspend operator fun invoke(addOnItemId: String): Resource<Boolean>{
        return addOnItemRepository.deleteAddOnItem( addOnItemId)
    }
}