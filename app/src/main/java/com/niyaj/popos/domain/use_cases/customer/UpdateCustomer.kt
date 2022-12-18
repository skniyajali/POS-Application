package com.niyaj.popos.domain.use_cases.customer

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCustomer(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(newCustomer: Customer, customerId: String): Resource<Boolean> {
        return customerRepository.updateCustomer(newCustomer, customerId)
    }
}