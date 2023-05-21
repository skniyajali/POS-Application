package com.niyaj.popos.features.main_feed.di

import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedProducts
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
    fun provideGetMainFeedProductsUseCases(mainFeedRepository: MainFeedRepository): GetMainFeedProducts {
        return GetMainFeedProducts(mainFeedRepository)
    }
}