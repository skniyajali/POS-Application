package com.niyaj.popos.features.delivery_partner.domain.use_cases.validation

import android.util.Patterns
import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.delivery_partner.domain.use_cases.PartnerUseCases
import javax.inject.Inject

class ValidatePartnerEmail @Inject constructor(
    private val partnerUseCases: PartnerUseCases
) {

    suspend fun execute(partnerEmail: String, partnerId: String? = null): ValidationResult {

        if(partnerEmail.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Email is required"
            )
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(partnerEmail).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "Email is invalid"
            )
        }

        when(val validationResult = partnerUseCases.getPartnerByEmail(partnerEmail, partnerId)){
            is Resource.Error -> {
                return ValidationResult(
                    successful = false,
                    errorMessage = validationResult.message ?: "Email already exists"
                )
            }
            is Resource.Loading -> {}
            is Resource.Success -> {}
        }

        return ValidationResult(
            successful = true
        )
    }
}