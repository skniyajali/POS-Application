package com.niyaj.popos.features.cart_order.di

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.CreateCardOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.DeleteCartOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.DeleteCartOrders
import com.niyaj.popos.features.cart_order.domain.use_cases.GetAllCartOrders
import com.niyaj.popos.features.cart_order.domain.use_cases.GetCartOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.GetLastCreatedOrderId
import com.niyaj.popos.features.cart_order.domain.use_cases.GetSelectedCartOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.PlaceAllOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.PlaceOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.SelectCartOrder
import com.niyaj.popos.features.cart_order.domain.use_cases.UpdateAddOnItemInCart
import com.niyaj.popos.features.cart_order.domain.use_cases.UpdateCartOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderCases(cartOrderRepository: CartOrderRepository): CartOrderUseCases {
        return CartOrderUseCases(
            getLastCreatedOrderId = GetLastCreatedOrderId(cartOrderRepository),
            getAllCartOrders = GetAllCartOrders(cartOrderRepository),
            getCartOrder = GetCartOrder(cartOrderRepository),
            getSelectedCartOrder = GetSelectedCartOrder(cartOrderRepository),
            selectCartOrder = SelectCartOrder(cartOrderRepository),
            createCardOrder = CreateCardOrder(cartOrderRepository),
            updateCartOrder = UpdateCartOrder(cartOrderRepository),
            updateAddOnItemInCart = UpdateAddOnItemInCart(cartOrderRepository),
            deleteCartOrder = DeleteCartOrder(cartOrderRepository),
            placeOrder = PlaceOrder(cartOrderRepository),
            placeAllOrder = PlaceAllOrder(cartOrderRepository),
            deleteCartOrders = DeleteCartOrders(cartOrderRepository),
        )
    }
}