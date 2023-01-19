package com.niyaj.popos.features.cart_order.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface CartOrderValidationRepository {

    fun validateCustomerAddress(orderType: String, customerAddress: String): ValidationResult

    fun validateCustomerPhone(orderType: String, customerPhone: String): ValidationResult

    fun validateOrderId(orderId: String): ValidationResult
}