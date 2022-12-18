package com.niyaj.popos.realm.addon_item.domain.use_cases

import com.niyaj.popos.realm.addon_item.domain.repository.AddOnItemRepository

class FindAddOnItemByName(
    private val addOnItemRepository: AddOnItemRepository
) {
    operator fun invoke(addOnItemName: String, addOnItemId: String?): Boolean {
        return addOnItemRepository.findAddOnItemByName(addOnItemName, addOnItemId)
    }
}