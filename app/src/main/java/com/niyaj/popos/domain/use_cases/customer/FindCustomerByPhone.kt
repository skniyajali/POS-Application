package com.niyaj.popos.domain.use_cases.customer

import com.niyaj.popos.domain.repository.CustomerRepository

class FindCustomerByPhone(
    private val customerRepository: CustomerRepository
) {

    operator fun invoke(customerPhone: String, customerId: String?) : Boolean {
        return customerRepository.findCustomerByPhone(customerPhone, customerId)
    }
}