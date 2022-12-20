package com.niyaj.popos.features.main_feed.di

import com.niyaj.popos.features.cart_order.domain.repository.CartOrderRepository
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedCategories
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedProducts
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedSelectedOrder
import com.niyaj.popos.features.main_feed.domain.use_cases.GetProductsPager
import com.niyaj.popos.features.main_feed.domain.use_cases.MainFeedUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainFeedModule {

    @Singleton
    @Provides
    fun provideMainFeedUseCases(mainFeedRepository: MainFeedRepository, cartOrderRepository: CartOrderRepository): MainFeedUseCases {
        return MainFeedUseCases(
            getMainFeedProducts = GetMainFeedProducts(mainFeedRepository),
            getMainFeedSelectedOrder = GetMainFeedSelectedOrder(mainFeedRepository, cartOrderRepository),
            getMainFeedCategories = GetMainFeedCategories(mainFeedRepository),
            getProductsPager = GetProductsPager(mainFeedRepository),
        )
    }
}