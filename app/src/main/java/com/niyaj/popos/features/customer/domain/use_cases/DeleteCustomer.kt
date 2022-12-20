package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.customer.domain.repository.CustomerRepository

class DeleteCustomer(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String): Resource<Boolean> {
        return customerRepository.deleteCustomer(customerId)
    }
}