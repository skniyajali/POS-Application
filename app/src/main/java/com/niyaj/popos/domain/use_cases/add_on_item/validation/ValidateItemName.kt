package com.niyaj.popos.domain.use_cases.add_on_item.validation

import com.niyaj.popos.domain.use_cases.add_on_item.AddOnItemUseCases
import com.niyaj.popos.domain.util.ValidationResult
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