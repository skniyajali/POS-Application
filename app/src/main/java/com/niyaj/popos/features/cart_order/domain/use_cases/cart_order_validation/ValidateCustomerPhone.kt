package com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCustomerPhone @Inject constructor(
    private val cartOrderValidationRepository: CartOrderValidationRepository
) {

    operator fun invoke(orderType: String = CartOrderType.DineIn.orderType, customerPhone: String): ValidationResult {
        return cartOrderValidationRepository.validateCustomerPhone(orderType, customerPhone)
    }
}