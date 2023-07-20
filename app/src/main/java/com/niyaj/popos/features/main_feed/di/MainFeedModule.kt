package com.niyaj.popos.features.main_feed.di

import com.niyaj.popos.features.main_feed.data.repository.MainFeedRepositoryImpl
import com.niyaj.popos.features.main_feed.domain.repository.MainFeedRepository
import com.niyaj.popos.features.main_feed.domain.use_cases.GetMainFeedProducts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainFeedModule {

    @Provides
    fun provideMainFeedRepositoryImpl(config : RealmConfiguration) : MainFeedRepository {
        return MainFeedRepositoryImpl(config)
    }

    @Singleton
    @Provides
    fun provideGetMainFeedProductsUseCases(
        mainFeedRepository : MainFeedRepository
    ) : GetMainFeedProducts {
        return GetMainFeedProducts(mainFeedRepository)
    }
}