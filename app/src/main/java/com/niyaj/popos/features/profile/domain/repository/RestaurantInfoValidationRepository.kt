package com.niyaj.popos.features.profile.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface RestaurantInfoValidationRepository {

    fun validatePaymentQrCode(paymentQrCode: String): ValidationResult

    fun validatePrimaryPhone(primaryPhone: String): ValidationResult

    fun validateRestaurantAddress(address: String): ValidationResult

    fun validateRestaurantEmail(email: String): ValidationResult

    fun validateRestaurantName(name: String): ValidationResult

    fun validateRestaurantTagline(tagline: String): ValidationResult

    fun validateSecondaryPhone(secondaryPhone: String): ValidationResult
}