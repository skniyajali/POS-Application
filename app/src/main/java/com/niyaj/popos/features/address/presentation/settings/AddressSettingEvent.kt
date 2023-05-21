package com.niyaj.popos.features.address.presentation.settings

import com.niyaj.popos.features.address.domain.model.Address
import com.niyaj.popos.utils.Constants.ImportExportType

sealed class AddressSettingEvent {

    data class SelectAddress(val addressId: String) : AddressSettingEvent()

    data class SelectAllAddress(val type: ImportExportType = ImportExportType.IMPORT) : AddressSettingEvent()

    object DeselectAddresses : AddressSettingEvent()

    object OnChooseAddress: AddressSettingEvent()

    data class ImportAddressesData(val addresses: List<Address> = emptyList()): AddressSettingEvent()

    object ClearImportedAddresses: AddressSettingEvent()

    object ImportAddresses: AddressSettingEvent()

    object GetExportedAddress: AddressSettingEvent()

    object GetAllAddress: AddressSettingEvent()

    object DeleteAllAddress: AddressSettingEvent()
}