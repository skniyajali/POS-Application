package com.niyaj.popos.features.customer.presentation.settings

sealed class CustomerSettingsEvent {
    object DeleteAllCustomer: CustomerSettingsEvent()
}
