package com.niyaj.popos.features.profile.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidatePaymentQrCode @Inject constructor() {

    fun validate(paymentQrCode: String): ValidationResult {

        if (paymentQrCode.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Restaurant payment QR code must not be empty"
            )
        }

        return ValidationResult(true)
    }
}