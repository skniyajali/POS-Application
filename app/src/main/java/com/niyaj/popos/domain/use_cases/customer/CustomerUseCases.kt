package com.niyaj.popos.domain.use_cases.customer

data class CustomerUseCases(
    val getAllCustomers: GetAllCustomers,
    val getCustomerById: GetCustomerById,
    val findCustomerByPhone: FindCustomerByPhone,
    val createNewCustomer: CreateNewCustomer,
    val updateCustomer: UpdateCustomer,
    val deleteCustomer: DeleteCustomer,
)
