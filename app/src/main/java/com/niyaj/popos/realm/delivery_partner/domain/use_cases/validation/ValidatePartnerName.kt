package com.niyaj.popos.realm.delivery_partner.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidatePartnerName @Inject constructor() {

    fun execute(partnerName: String): ValidationResult {

        if(partnerName.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Name is required"
            )
        }

        if(partnerName.length < 4){
            return ValidationResult(
                successful = false,
                errorMessage = "Name must be at least 4 characters long"
            )
        }
        if(partnerName.any { it.isDigit() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Name must not contain any digit"
            )
        }


        return ValidationResult(
            successful = true
        )
    }
}