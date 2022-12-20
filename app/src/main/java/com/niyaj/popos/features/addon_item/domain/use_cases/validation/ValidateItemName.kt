package com.niyaj.popos.features.addon_item.domain.use_cases.validation

import com.niyaj.popos.features.addon_item.domain.use_cases.AddOnItemUseCases
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateItemName @Inject constructor(
    private val addOnItemUseCases: AddOnItemUseCases
) {

    fun execute(name: String, addOnItemId: String?): ValidationResult {

        if(name.isEmpty()) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item name must not be empty.",
            )
        }

        val result = name.any { it.isDigit() }

        if(result) {
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item name must not contains any digit.",
            )
        }
        val serverResult = addOnItemUseCases.findAddOnItemByName(name, addOnItemId)

        if(serverResult){
            return ValidationResult(
                successful = false,
                errorMessage = "AddOn item name already exists.",
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}