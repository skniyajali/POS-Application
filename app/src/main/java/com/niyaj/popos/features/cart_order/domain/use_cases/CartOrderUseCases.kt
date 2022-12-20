package com.niyaj.popos.features.cart_order.domain.use_cases

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
    val deleteCartOrders: DeleteCartOrders
)