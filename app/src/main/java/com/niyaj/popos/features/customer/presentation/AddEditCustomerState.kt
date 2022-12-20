package com.niyaj.popos.features.customer.presentation

data class AddEditCustomerState(

    val customerPhone: String = "",

    val customerPhoneError: String? = null,

    val customerName: String? = null,

    val customerNameError: String? = null,

    val customerEmail: String? = null,

    val customerEmailError: String? = null,
)
