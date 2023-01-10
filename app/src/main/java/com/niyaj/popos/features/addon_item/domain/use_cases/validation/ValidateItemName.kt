package com.niyaj.popos.features.addon_item.domain.use_cases.validation

import com.niyaj.popos.features.addon_item.domain.repository.ValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateItemName @Inject constructor(
    private val validationRepository: ValidationRepository
) {
    operator fun invoke(name: String, addOnItemId: String? = null): ValidationResult {
        return validationRepository.validateItemName(name, addOnItemId)
    }
}