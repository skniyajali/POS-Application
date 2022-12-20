package com.niyaj.popos.features.cart.di

import com.niyaj.popos.features.cart.domain.repository.CartRepository
import com.niyaj.popos.features.cart.domain.use_cases.AddProductToCart
import com.niyaj.popos.features.cart.domain.use_cases.CartUseCases
import com.niyaj.popos.features.cart.domain.use_cases.DeleteCartItem
import com.niyaj.popos.features.cart.domain.use_cases.GetAllCartItems
import com.niyaj.popos.features.cart.domain.use_cases.GetAllDineInOrders
import com.niyaj.popos.features.cart.domain.use_cases.GetAllDineOutOrders
import com.niyaj.popos.features.cart.domain.use_cases.GetMainFeedProductQuantity
import com.niyaj.popos.features.cart.domain.use_cases.RemoveProductFromCart
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CartModule {

    @Provides
    @Singleton
    fun provideCartCases(cartRepository: CartRepository): CartUseCases {
        return CartUseCases(
            getAllDineInOrders = GetAllDineInOrders(cartRepository),
            getAllDineOutOrders = GetAllDineOutOrders(cartRepository),
            getAllCartItems = GetAllCartItems(cartRepository),
            addProductToCart = AddProductToCart(cartRepository),
            removeProductFromCart = RemoveProductFromCart(cartRepository),
            deleteCartItem = DeleteCartItem(cartRepository),
            getMainFeedProductQuantity = GetMainFeedProductQuantity(cartRepository),
        )
    }
}