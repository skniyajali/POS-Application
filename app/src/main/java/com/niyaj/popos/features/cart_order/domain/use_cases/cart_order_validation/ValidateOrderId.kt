package com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation

import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateOrderId @Inject constructor() {
    fun execute(orderId: String): ValidationResult {

        if(orderId.isEmpty()){
            return ValidationResult(
                successful = false,
                errorMessage = "The order id must not be empty"
            )
        }

        if(orderId.length < 6) {
            return ValidationResult(
                successful = false,
                errorMessage = "The order id must be 6 characters long"
            )
        }

        return ValidationResult(
            successful = true
        )
    }
}