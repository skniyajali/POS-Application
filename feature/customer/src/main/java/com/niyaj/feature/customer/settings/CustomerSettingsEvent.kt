package com.niyaj.feature.customer.settings

import com.niyaj.common.utils.Constants.ImportExportType
import com.niyaj.model.Customer

sealed class CustomerSettingsEvent {

    data class SelectCustomer(val customerId: String) : CustomerSettingsEvent()

    data class SelectAllCustomer(val type: ImportExportType = ImportExportType.IMPORT): CustomerSettingsEvent()

    data object DeselectCustomers : CustomerSettingsEvent()

    data object OnChooseCustomer: CustomerSettingsEvent()

    data class ImportCustomerData(val customers: List<Customer> = emptyList()): CustomerSettingsEvent()

    data object ClearImportedCustomer: CustomerSettingsEvent()

    data object ImportCustomers: CustomerSettingsEvent()

    data object GetExportedCustomer: CustomerSettingsEvent()

    data object GetAllCustomer: CustomerSettingsEvent()

    data object DeleteAllCustomer: CustomerSettingsEvent()
}
