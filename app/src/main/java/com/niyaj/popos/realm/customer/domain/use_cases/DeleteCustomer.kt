package com.niyaj.popos.realm.customer.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.customer.domain.repository.CustomerRepository

class DeleteCustomer(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String): Resource<Boolean> {
        return customerRepository.deleteCustomer(customerId)
    }
}