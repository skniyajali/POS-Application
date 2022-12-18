package com.niyaj.popos.domain.use_cases.customer

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource

class GetCustomerById(
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(customerId: String): Resource<Customer?> {
        return customerRepository.getCustomerById(customerId)
    }
}