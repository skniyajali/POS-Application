package com.niyaj.popos.domain.use_cases.charges.validation

import com.niyaj.popos.domain.util.ValidationResult
import javax.inject.Inject

class ValidateChargesPrice @Inject constructor() {

    fun execute(doesApplicable: Boolean, chargesPrice: Int): ValidationResult {
        if(doesApplicable) {
            if(chargesPrice == 0){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Charges Price required"
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}