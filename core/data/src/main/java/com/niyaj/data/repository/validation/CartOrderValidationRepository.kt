package com.niyaj.data.repository.validation

import com.niyaj.common.utils.ValidationResult
import com.niyaj.model.OrderType

interface CartOrderValidationRepository {

    fun validateCustomerAddress(orderType: OrderType, customerAddress: String): ValidationResult

    fun validateCustomerPhone(orderType: OrderType, customerPhone: String): ValidationResult

    suspend fun validateOrderId(orderId: String, cartOrderId: String): ValidationResult
}