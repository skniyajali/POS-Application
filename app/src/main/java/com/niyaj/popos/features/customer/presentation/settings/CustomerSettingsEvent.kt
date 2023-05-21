package com.niyaj.popos.features.customer.presentation.settings

import com.niyaj.popos.features.customer.domain.model.Customer
import com.niyaj.popos.utils.Constants.ImportExportType

sealed class CustomerSettingsEvent {

    data class SelectCustomer(val customerId: String) : CustomerSettingsEvent()

    data class SelectAllCustomer(val type: ImportExportType = ImportExportType.IMPORT): CustomerSettingsEvent()

    object DeselectCustomers : CustomerSettingsEvent()

    object OnChooseCustomer: CustomerSettingsEvent()

    data class ImportCustomerData(val customers: List<Customer> = emptyList()): CustomerSettingsEvent()

    object ClearImportedCustomer: CustomerSettingsEvent()

    object ImportCustomers: CustomerSettingsEvent()

    object GetExportedCustomer: CustomerSettingsEvent()

    object GetAllCustomer: CustomerSettingsEvent()

    object DeleteAllCustomer: CustomerSettingsEvent()
}
