package com.niyaj.popos.domain.use_cases.cart_order

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