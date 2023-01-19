package com.niyaj.popos.features.customer.domain.use_cases

import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerEmail
import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerName
import com.niyaj.popos.features.customer.domain.use_cases.validation.ValidateCustomerPhone

data class CustomerUseCases(
    val validateCustomerName: ValidateCustomerName,
    val validateCustomerEmail: ValidateCustomerEmail,
    val validateCustomerPhone: ValidateCustomerPhone,
    val getAllCustomers: GetAllCustomers,
    val getCustomerById: GetCustomerById,
    val createNewCustomer: CreateNewCustomer,
    val updateCustomer: UpdateCustomer,
    val deleteCustomer: DeleteCustomer,
    val deleteAllCustomers: DeleteAllCustomers,
    val importContacts: ImportContacts
)
