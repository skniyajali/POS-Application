package com.niyaj.popos.domain.use_cases.customer

import com.niyaj.popos.domain.model.Customer
import com.niyaj.popos.domain.repository.CustomerRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewCustomer(
    private val customerRepository: CustomerRepository
) {

    suspend operator fun invoke(customer: Customer): Resource<Boolean> {
        return customerRepository.createNewCustomer(customer)
    }
}