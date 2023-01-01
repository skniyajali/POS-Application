package com.niyaj.popos.features.customer.domain.use_cases

data class CustomerUseCases(
    val getAllCustomers: GetAllCustomers,
    val getCustomerById: GetCustomerById,
    val findCustomerByPhone: FindCustomerByPhone,
    val createNewCustomer: CreateNewCustomer,
    val updateCustomer: UpdateCustomer,
    val deleteCustomer: DeleteCustomer,
    val deleteAllCustomers: DeleteAllCustomers,
    val importContacts: ImportContacts
)
