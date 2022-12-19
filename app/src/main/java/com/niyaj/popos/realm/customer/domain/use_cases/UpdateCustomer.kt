package com.niyaj.popos.realm.customer.domain.use_cases

import com.niyaj.popos.realm.customer.domain.model.Customer
import com.niyaj.popos.realm.customer.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource

class UpdateCustomer(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(newCustomer: Customer, customerId: String): Resource<Boolean> {
        return customerRepository.updateCustomer(newCustomer, customerId)
    }
}