package com.niyaj.popos.domain.use_cases.add_on_item

import com.niyaj.popos.domain.model.AddOnItem
import com.niyaj.popos.domain.repository.AddOnItemRepository
import com.niyaj.popos.domain.util.Resource

class GetAddOnItemById(
    private val addOnItemRepository: AddOnItemRepository
) {

    suspend operator fun invoke(addOnItemId: String): Resource<AddOnItem?>{
        return addOnItemRepository.getAddOnItemById(addOnItemId)
    }
}