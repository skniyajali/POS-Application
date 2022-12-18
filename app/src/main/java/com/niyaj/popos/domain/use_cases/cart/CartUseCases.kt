package com.niyaj.popos.domain.use_cases.cart

data class CartUseCases(
    val getAllCartItems: GetAllCartItems,
    val getAllDineInOrders: GetAllDineInOrders,
    val getAllDineOutOrders: GetAllDineOutOrders,
    val getSelectedCartItems: GetSelectedCartItems,
    val selectCartItem: SelectCartItem,
    val deleteCartItem: DeleteCartItem,
    val selectAllCartItem: SelectAllCartItem,
    val addProductToCart: AddProductToCart,
    val removeProductFromCart: RemoveProductFromCart,
    val getMainFeedProductQuantity: GetMainFeedProductQuantity,
)
