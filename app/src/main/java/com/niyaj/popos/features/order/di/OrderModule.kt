package com.niyaj.popos.features.order.di

import com.niyaj.popos.features.order.data.repository.OrderRepositoryImpl
import com.niyaj.popos.features.order.domain.repository.OrderRepository
import com.niyaj.popos.features.order.domain.use_cases.GetAllDineInOrders
import com.niyaj.popos.features.order.domain.use_cases.GetAllDineOutOrders
import com.niyaj.popos.features.order.domain.use_cases.OrderUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderModule {

    @Provides
    fun provideOrderRepositoryImpl(config: RealmConfiguration): OrderRepository {
        return OrderRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideOrderCases(orderRepository: OrderRepository): OrderUseCases {
        return OrderUseCases(
            getAllDineInOrders = GetAllDineInOrders(orderRepository),
            getAllDineOutOrders = GetAllDineOutOrders(orderRepository)
        )
    }
}