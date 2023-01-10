package com.niyaj.popos.features.addon_item.domain.use_cases

import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.addon_item.domain.model.InvalidAddOnItemException
import com.niyaj.popos.features.addon_item.domain.repository.AddOnItemRepository
import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.common.util.Resource
import javax.inject.Inject

class CreateNewAddOnItem @Inject constructor(
    private val addOnItemRepository: AddOnItemRepository,
    private val validationRepository: ValidationRepository,
) {
    @Throws(InvalidAddOnItemException::class)
    suspend operator fun invoke(newAddOnItem: AddOnItem): Resource<Boolean> {
        val nameResult = validationRepository.validateItemName(newAddOnItem.itemName, newAddOnItem.addOnItemId)
        val priceResult = validationRepository.validateItemPrice(newAddOnItem.itemPrice)

        if (nameResult.errorMessage != null) {
            throw InvalidAddOnItemException(nameResult.errorMessage)
        }

        if (priceResult.errorMessage != null) {
            throw InvalidAddOnItemException(priceResult.errorMessage)
        }

        return addOnItemRepository.createNewAddOnItem(newAddOnItem)
    }
}