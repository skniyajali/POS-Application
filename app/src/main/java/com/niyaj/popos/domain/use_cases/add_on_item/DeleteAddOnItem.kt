package com.niyaj.popos.domain.use_cases.add_on_item

import com.niyaj.popos.domain.repository.AddOnItemRepository
import com.niyaj.popos.domain.util.Resource

class DeleteAddOnItem(
    private val addOnItemRepository: AddOnItemRepository
) {

    suspend operator fun invoke(addOnItemId: String): Resource<Boolean>{
        return addOnItemRepository.deleteAddOnItem( addOnItemId)
    }
}