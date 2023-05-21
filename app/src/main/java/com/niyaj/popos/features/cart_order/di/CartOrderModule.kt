package com.niyaj.popos.features.cart_order.di

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.GetAllCartOrders
import com.niyaj.popos.features.cart_order.domain.use_cases.GetSelectedCartOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderCases(
        cartOrderRepository: CartOrderRepository
    ): CartOrderUseCases {
        return CartOrderUseCases(
            getAllCartOrders = GetAllCartOrders(cartOrderRepository),
            getSelectedCartOrder = GetSelectedCartOrder(cartOrderRepository),
        )
    }
}