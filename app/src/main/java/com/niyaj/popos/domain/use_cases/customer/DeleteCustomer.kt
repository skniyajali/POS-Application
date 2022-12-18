package com.niyaj.popos.domain.use_cases.customer

import com.niyaj.popos.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource

class DeleteCustomer(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String): Resource<Boolean> {
        return customerRepository.deleteCustomer(customerId)
    }
}