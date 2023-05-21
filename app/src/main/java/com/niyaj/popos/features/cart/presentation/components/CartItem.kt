package com.niyaj.popos.features.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.niyaj.popos.features.addon_item.domain.model.AddOnItem
import com.niyaj.popos.features.cart.domain.model.CartItem
import com.niyaj.popos.features.common.ui.theme.ProfilePictureSizeSmall
import com.niyaj.popos.features.common.ui.theme.SpaceMini
import com.niyaj.popos.features.common.ui.theme.SpaceSmall


@Composable
fun CartItems(
    listState: LazyListState,
    cartItems: List<CartItem>,
    selectedCartItems: List<String>,
    addOnItems: List<AddOnItem>,
    showPrintBtn: Boolean = false,
    onSelectCartOrder: (String) -> Unit,
    onClickEditOrder: (String) -> Unit,
    onClickViewOrder: (String) -> Unit,
    onClickDecreaseQty: (cartOrderId: String, productId: String) -> Unit,
    onClickIncreaseQty: (cartOrderId: String, productId: String) -> Unit,
    onClickAddOnItem: (addOnItemId: String, cartOrderId: String) -> Unit,
    onClickPlaceOrder: (cartOrderId: String) -> Unit,
    onClickPrintOrder: (cartOrderId: String) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SpaceSmall),
        verticalArrangement = Arrangement.Top,
        state = listState,
    ) {
        itemsIndexed(
            items = cartItems,
            key = { index, cartItem ->
                cartItem.cartOrderId.plus(index)
            }
        ){ index, cartItem ->
            if(cartItem.cartProducts.isNotEmpty()) {
                CartItem(
                    cartItem = cartItem,
                    doesSelected = selectedCartItems.contains(cartItem.cartOrderId),
                    addOnItems = addOnItems,
                    showPrintBtn = showPrintBtn,
                    onSelectCartOrder = onSelectCartOrder,
                    onClickEditOrder = onClickEditOrder,
                    onClickViewOrder = onClickViewOrder,
                    onClickDecreaseQty = onClickDecreaseQty,
                    onClickIncreaseQty = onClickIncreaseQty,
                    onClickAddOnItem = onClickAddOnItem,
                    onClickPlaceOrder = onClickPlaceOrder,
                    onClickPrintOrder = onClickPrintOrder
                )

                Spacer(modifier = Modifier.height(SpaceSmall))

                if (index == cartItems.size - 1) {
                    Spacer(modifier = Modifier.height(ProfilePictureSizeSmall))
                }
            }
        }
    }
}


@Composable
fun CartItem(
    cartItem: CartItem,
    doesSelected: Boolean,
    addOnItems: List<AddOnItem>,
    showPrintBtn: Boolean = false,
    onSelectCartOrder: (String) -> Unit,
    onClickEditOrder: (String) -> Unit,
    onClickViewOrder: (String) -> Unit,
    onClickDecreaseQty: (cartOrderId: String, productId: String) -> Unit,
    onClickIncreaseQty: (cartOrderId: String, productId: String) -> Unit,
    onClickAddOnItem: (addOnItemId: String, cartOrderId: String) -> Unit,
    onClickPlaceOrder: (cartOrderId: String) -> Unit,
    onClickPrintOrder: (cartOrderId: String) -> Unit = {}
) {
    val newOrderId = if(!cartItem.customerAddress.isNullOrEmpty()){
        cartItem.customerAddress.uppercase().plus(" -")
            .plus(cartItem.orderId)
    }else{
        cartItem.orderId
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colors.surface,
                RoundedCornerShape(6.dp)
            )
            .clickable {
                onSelectCartOrder(cartItem.cartOrderId)
            },
        elevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            CartItemOrderDetailsSection(
                orderId = newOrderId,
                orderType =  cartItem.orderType,
                customerPhone = cartItem.customerPhone,
                selected = doesSelected,
                onClick = {
                    onSelectCartOrder(cartItem.cartOrderId)
                },
                onEditClick = {
                    onClickEditOrder(cartItem.cartOrderId)
                },
                onViewClick = {
                    onClickViewOrder(cartItem.cartOrderId)
                }
            )

            Spacer(modifier = Modifier.height(SpaceMini))

            CartItemProductDetailsSection(
                cartProducts = cartItem.cartProducts,
                decreaseQuantity = {
                    onClickDecreaseQty(cartItem.cartOrderId, it)
                },
                increaseQuantity = {
                    onClickIncreaseQty(cartItem.cartOrderId, it)
                }
            )


            if(addOnItems.isNotEmpty()){
                Spacer(modifier = Modifier.height(SpaceSmall))

                CartAddOnItems(
                    addOnItems = addOnItems,
                    selectedAddOnItem = cartItem.addOnItems,
                    onClick = {
                        onClickAddOnItem(it, cartItem.cartOrderId)
                    },
                )
            }

            Spacer(modifier = Modifier.height(SpaceSmall))

            CartItemTotalPriceSection(
                itemCount = cartItem.cartProducts.size,
                orderType = cartItem.orderType,
                totalPrice = cartItem.orderPrice.first,
                discountPrice = cartItem.orderPrice.second,
                showPrintBtn = showPrintBtn,
                onClickPlaceOrder = {
                    onClickPlaceOrder(cartItem.cartOrderId)
                },
                onClickPrintOrder = {
                    onClickPrintOrder(cartItem.cartOrderId)
                }
            )
        }
    }
}