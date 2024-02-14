package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult

interface RestaurantInfoValidationRepository {

    fun validateRestaurantName(name: String): ValidationResult

    fun validateRestaurantEmail(email: String): ValidationResult

    fun validatePrimaryPhone(primaryPhone: String): ValidationResult

    fun validatePassword(password: String): ValidationResult

    fun validateSecondaryPhone(secondaryPhone: String): ValidationResult

    fun validateRestaurantDesc(description: String): ValidationResult

    fun validateRestaurantAddress(address: String): ValidationResult

    fun validateRestaurantTagline(tagline: String): ValidationResult

    fun validatePaymentQrCode(paymentQrCode: String): ValidationResult

}