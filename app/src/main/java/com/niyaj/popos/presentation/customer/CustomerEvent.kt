package com.niyaj.popos.presentation.customer

import com.niyaj.popos.domain.util.filter_items.FilterCustomer

sealed class CustomerEvent{

    data class CustomerNameChanged(val customerName: String) : CustomerEvent()

    data class CustomerEmailChanged(val customerEmail: String) : CustomerEvent()

    data class CustomerPhoneChanged(val customerPhone: String) : CustomerEvent()

    data class SelectCustomer(val customerId: String) : CustomerEvent()

    object SelectAllCustomer : CustomerEvent()

    object DeselectAllCustomer : CustomerEvent()

    object CreateNewCustomer : CustomerEvent()

    data class UpdateCustomer(val customerId: String) : CustomerEvent()

    data class DeleteCustomer(val customers: List<String>) : CustomerEvent()

    data class OnFilterCustomer(val filterCustomer: FilterCustomer): CustomerEvent()

    data class OnSearchCustomer(val searchText: String): CustomerEvent()

    object ToggleSearchBar : CustomerEvent()

    object RefreshCustomer : CustomerEvent()
}
