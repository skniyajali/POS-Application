package com.niyaj.popos.features.cart.domain.use_cases

data class CartUseCases(
    val getAllCartItems: GetAllCartItems,
    val getAllDineInOrders: GetAllDineInOrders,
    val getAllDineOutOrders: GetAllDineOutOrders,
    val deleteCartItem: DeleteCartItem,
    val addProductToCart: AddProductToCart,
    val removeProductFromCart: RemoveProductFromCart,
    val getMainFeedProductQuantity: GetMainFeedProductQuantity,
)
