package com.niyaj.popos.features.customer.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface CustomerValidationRepository {

    fun validateCustomerName(customerName: String? = null): ValidationResult

    fun validateCustomerEmail(customerEmail: String? = null): ValidationResult

    fun validateCustomerPhone(customerPhone: String, customerId: String? = null): ValidationResult
}