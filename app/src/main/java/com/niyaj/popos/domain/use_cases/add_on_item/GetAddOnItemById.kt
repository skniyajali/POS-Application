package com.niyaj.popos.domain.use_cases.add_on_item

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.add_on_items.AddOnItem
import com.niyaj.popos.realm.add_on_items.AddOnItemRepository

class GetAddOnItemById(
    private val addOnItemRepository: AddOnItemRepository
) {

    suspend operator fun invoke(addOnItemId: String): Resource<AddOnItem?>{
        return addOnItemRepository.getAddOnItemById(addOnItemId)
    }
}