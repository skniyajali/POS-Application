package com.niyaj.feature.cart_order.settings

sealed class CartOrderSettingsEvent {
    data object DeletePastSevenDaysBeforeData: CartOrderSettingsEvent()

    data object DeleteAllCartOrders: CartOrderSettingsEvent()
}
