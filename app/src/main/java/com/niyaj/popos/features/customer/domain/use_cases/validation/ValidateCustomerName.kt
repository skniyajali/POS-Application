package com.niyaj.popos.features.customer.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.customer.domain.repository.CustomerValidationRepository
import javax.inject.Inject

class ValidateCustomerName @Inject constructor(
    private val customerValidationRepository: CustomerValidationRepository
) {

    operator fun invoke(customerName: String?): ValidationResult {
        return customerValidationRepository.validateCustomerName(customerName)
    }
}