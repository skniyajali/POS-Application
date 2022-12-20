package com.niyaj.popos.features.delivery_partner.domain.use_cases.validation

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.delivery_partner.domain.use_cases.PartnerUseCases
import javax.inject.Inject

class ValidatePartnerPhone @Inject constructor(
    private val partnerUseCases: PartnerUseCases
) {

    suspend fun execute(partnerPhone: String, partnerId: String? = null): ValidationResult {

        if(partnerPhone.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no is required"
            )
        }

        if(partnerPhone.length < 10 || partnerPhone.length > 10) {
            return ValidationResult(
                successful = false,
                errorMessage = "The phone no must be 10 digits",
            )
        }

        if(partnerPhone.any { it.isLetter() }){
            return ValidationResult(
                successful = false,
                errorMessage = "Phone no must not contain any letter"
            )
        }

        when(val validationResult = partnerUseCases.getPartnerByPhone(partnerPhone, partnerId)){
            is Resource.Error -> {
                return ValidationResult(
                    false,
                    errorMessage = validationResult.message ?: "Phone no already exists",
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