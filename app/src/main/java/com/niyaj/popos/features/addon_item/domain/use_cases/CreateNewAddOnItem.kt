package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.common.util.Resource

class CreateNewAddOnItem(
    private val addOnItemRepository: AddOnItemRepository
) {
    suspend operator fun invoke(newAddOnItem: AddOnItem): Resource<Boolean> {
        return addOnItemRepository.createNewAddOnItem(newAddOnItem)
    }
}