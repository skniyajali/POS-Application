package com.niyaj.popos.features.cart_order.di

import com.niyaj.popos.applicationScope
import com.niyaj.popos.features.app_settings.domain.repository.SettingsRepository
import com.niyaj.popos.features.cart_order.data.repository.CartOrderRepositoryImpl
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.cart_order.domain.repository.CartOrderValidationRepository
import com.niyaj.popos.features.cart_order.domain.use_cases.CartOrderUseCases
import com.niyaj.popos.features.cart_order.domain.use_cases.GetAllCartOrders
import com.niyaj.popos.features.cart_order.domain.use_cases.GetSelectedCartOrder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration

@Module
@InstallIn(SingletonComponent::class)
object CartOrderModule {

    @Provides
    fun provideCartOrderRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ): CartOrderRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, applicationScope)
    }

    @Provides
    fun provideCartOrderValidationRepositoryImpl(
        config : RealmConfiguration,
        settingsRepository : SettingsRepository
    ): CartOrderValidationRepository {
        return CartOrderRepositoryImpl(config, settingsRepository, applicationScope)
    }

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