package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.common.util.Resource

class DeleteAddOnItem(
    private val addOnItemRepository: AddOnItemRepository
) {
    suspend operator fun invoke(addOnItemId: String): Resource<Boolean> {
        return addOnItemRepository.deleteAddOnItem( addOnItemId)
    }
}