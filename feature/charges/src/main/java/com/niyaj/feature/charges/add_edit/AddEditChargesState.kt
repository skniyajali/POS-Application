package com.niyaj.feature.charges.add_edit

data class AddEditChargesState(
    val chargesName: String = "",
    val chargesPrice: String = "",
    val chargesApplicable: Boolean = false,
)
