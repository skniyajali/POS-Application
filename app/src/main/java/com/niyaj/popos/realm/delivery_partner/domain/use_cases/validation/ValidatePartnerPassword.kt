package com.niyaj.popos.realm.delivery_partner.domain.use_cases.validation

import com.niyaj.popos.domain.util.ValidationResult
import com.niyaj.popos.util.isValidPassword
import javax.inject.Inject

class ValidatePartnerPassword @Inject constructor() {

    fun execute(partnerPassword: String): ValidationResult {

        if(partnerPassword.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Password is required"
            )
        }

        if(!isValidPassword(partnerPassword)){
            return ValidationResult(
                successful = false,
                errorMessage = "Password must be at least 8 characters long and it must contain a lowercase & uppercase letter and at least one special character and one digit."
            )
        }


        return ValidationResult(
            successful = true
        )
    }
}