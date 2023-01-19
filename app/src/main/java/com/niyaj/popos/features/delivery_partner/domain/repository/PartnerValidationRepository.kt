package com.niyaj.popos.features.delivery_partner.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface PartnerValidationRepository {

    fun validatePartnerName(partnerName: String): ValidationResult

    fun validatePartnerEmail(partnerEmail: String, partnerId: String? = null): ValidationResult

    fun validatePartnerPassword(partnerPassword: String): ValidationResult

    fun validatePartnerPhone(partnerPhone: String, partnerId: String? = null): ValidationResult
}