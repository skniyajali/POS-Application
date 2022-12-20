package com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation

import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCustomerAddress @Inject constructor() {

    fun execute(orderType: String = CartOrderType.DineIn.orderType, customerAddress: String): ValidationResult {
        if(orderType != CartOrderType.DineIn.orderType) {
            if(customerAddress.isEmpty()) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "Customer address must not be empty"
                )
            }
            if(customerAddress.length < 2) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "The address must be more than 2 characters long"
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}