package com.niyaj.popos.presentation.address.add_edit

data class AddEditAddressState(
    val shortName: String = "",
    val shortNameError: String? = null,
    
    val address: String = "",
    val addressError: String? = null
)
