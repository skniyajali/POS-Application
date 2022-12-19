package com.niyaj.popos.realm.charges.presentation

data class AddEditChargesState(
    val chargesName: String = "",
    val chargesNameError: String? = null,
    val chargesPrice: String = "",
    val chargesPriceError: String? = null,
    val chargesApplicable: Boolean = false,
)
