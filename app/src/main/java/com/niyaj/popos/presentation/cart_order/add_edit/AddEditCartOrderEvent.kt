package com.niyaj.popos.presentation.cart_order.add_edit

sealed class AddEditCartOrderEvent {

    data class OrderIdChanged(val orderId: String) : AddEditCartOrderEvent()

    data class OrderTypeChanged(val orderType: String) : AddEditCartOrderEvent()

    data class CustomerPhoneChanged(val customerPhone: String, val customerId: String? = null) : AddEditCartOrderEvent()

    data class CustomerAddressChanged(val customerAddress: String, val addressId: String? = null) : AddEditCartOrderEvent()

    data class OnSearchAddress(val searchText: String): AddEditCartOrderEvent()

    data class OnSearchCustomer(val searchText: String): AddEditCartOrderEvent()

    object OnClearCustomer : AddEditCartOrderEvent()

    object OnClearAddress : AddEditCartOrderEvent()

    data class OnUpdateCartOrder(val cartOrderId: String): AddEditCartOrderEvent()

    object CreateNewCartOrder: AddEditCartOrderEvent()

    data class UpdateCartOrder(val cartOrderId: String): AddEditCartOrderEvent()

    object ResetFields: AddEditCartOrderEvent()

    object GetAndSetCartOrderId: AddEditCartOrderEvent()
}
