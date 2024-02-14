package com.niyaj.model

import com.niyaj.common.utils.Constants.SELECTED_CART_ORDER_ID

data class SelectedCartOrder(
    val selectedCartId: String = SELECTED_CART_ORDER_ID,

    val cartOrder: CartOrder? = null,
)
