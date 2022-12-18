package com.niyaj.popos.realm.addon_item.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.addon_item.domain.model.AddOnItem
import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository

class UpdateAddOnItem(
    private val addOnItemRepository: AddOnItemRepository
) {

    suspend operator fun invoke(newAddOnItem: AddOnItem, addOnItemId: String): Resource<Boolean> {
        return addOnItemRepository.updateAddOnItem(newAddOnItem, addOnItemId)
    }
}