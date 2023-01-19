package com.niyaj.popos.features.cart_order.domain.use_cases

import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateCustomerAddress
import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateCustomerPhone
import com.niyaj.popos.features.cart_order.domain.use_cases.cart_order_validation.ValidateOrderId

data class CartOrderUseCases(
    val getLastCreatedOrderId: GetLastCreatedOrderId,
    val getAllCartOrders: GetAllCartOrders,
    val getCartOrder: GetCartOrder,
    val getSelectedCartOrder: GetSelectedCartOrder,
    val selectCartOrder: SelectCartOrder,
    val createCardOrder: CreateCardOrder,
    val updateCartOrder: UpdateCartOrder,
    val updateAddOnItemInCart: UpdateAddOnItemInCart,
    val deleteCartOrder: DeleteCartOrder,
    val placeOrder: PlaceOrder,
    val placeAllOrder: PlaceAllOrder,
    val deleteCartOrders: DeleteCartOrders,
    val validateCustomerAddress: ValidateCustomerAddress,
    val validateCustomerPhone: ValidateCustomerPhone,
    val validateOrderId: ValidateOrderId,
)