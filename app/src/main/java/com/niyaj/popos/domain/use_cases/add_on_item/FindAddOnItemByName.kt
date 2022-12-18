package com.niyaj.popos.domain.use_cases.add_on_item

import com.niyaj.popos.domain.repository.AddOnItemRepository

class FindAddOnItemByName(
    private val addOnItemRepository: AddOnItemRepository
) {
    operator fun invoke(addOnItemName: String, addOnItemId: String?): Boolean {
        return addOnItemRepository.findAddOnItemByName(addOnItemName, addOnItemId)
    }
}