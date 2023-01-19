package com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateOrderId @Inject constructor(
    private val cartOrderValidationRepository: CartOrderValidationRepository
) {
    operator fun invoke(orderId: String): ValidationResult {
        return cartOrderValidationRepository.validateOrderId(orderId)
    }
}