package com.niyaj.popos.features.customer.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import javax.inject.Inject

class ValidateCustomerPhone @Inject constructor(
    private val customerValidationRepository: CustomerValidationRepository
) {
    operator fun invoke(customerPhone: String, customerId: String?): ValidationResult {
        return customerValidationRepository.validateCustomerPhone(customerPhone, customerId)
    }
}