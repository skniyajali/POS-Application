package com.niyaj.popos.features.cart_order.presentation.settings

sealed class CartOrderSettingsEvent {
    object DeletePastSevenDaysBeforeData: CartOrderSettingsEvent()

    object DeleteAllCartOrders: CartOrderSettingsEvent()
}
