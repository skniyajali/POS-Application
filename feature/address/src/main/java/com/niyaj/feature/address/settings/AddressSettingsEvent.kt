package com.niyaj.feature.address.settings

import com.niyaj.model.Address

sealed class AddressSettingsEvent {

    data object OnChooseItems: AddressSettingsEvent()

    data object GetExportedItems: AddressSettingsEvent()

    data class OnImportAddressItemsFromFile(val data: List<Address>): AddressSettingsEvent()

    data object ClearImportedAddresses: AddressSettingsEvent()

    data object ImportAddressItemsToDatabase: AddressSettingsEvent()
}