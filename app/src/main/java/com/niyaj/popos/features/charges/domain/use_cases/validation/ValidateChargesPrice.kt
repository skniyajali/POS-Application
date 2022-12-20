package com.niyaj.popos.features.charges.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
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