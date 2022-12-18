package com.niyaj.popos.domain.use_cases.delivery_partner.validation

import android.util.Patterns
import com.niyaj.popos.domain.use_cases.delivery_partner.PartnerUseCases
import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.domain.util.ValidationResult
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