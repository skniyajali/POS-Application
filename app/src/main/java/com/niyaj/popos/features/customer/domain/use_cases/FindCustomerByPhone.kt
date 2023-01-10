package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.customer.domain.repository.CustomerRepository

class FindCustomerByPhone(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(customerPhone: String, customerId: String?) : Boolean {
        return customerRepository.findCustomerByPhone(customerPhone, customerId)
    }
}