package com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation

import com.niyaj.popos.features.cart_order.domain.util.CartOrderType
import com.niyaj.popos.features.common.util.ValidationResult
import javax.inject.Inject

class ValidateCustomerPhone @Inject constructor() {

    fun execute(orderType: String = CartOrderType.DineIn.orderType, customerPhone: String): ValidationResult {

        if(orderType != CartOrderType.DineIn.orderType){
            if(customerPhone.isEmpty()){
                return ValidationResult(
                    successful = false,
                    errorMessage = "Phone no must not be empty",
                )
            }
            if(customerPhone.length != 10) {
                return ValidationResult(
                    successful = false,
                    errorMessage = "The phone no must be 10 digits long"
                )
            }

            val containsLetters = customerPhone.any { it.isLetter() }

            if(containsLetters){
                return ValidationResult(
                    successful = false,
                    errorMessage = "The phone no does not contains any characters"
                )
            }
        }

        return ValidationResult(
            successful = true
        )
    }
}