package com.niyaj.popos.features.category.di

import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    @Singleton
    fun provideGetAllCategoryCases(categoryRepository: CategoryRepository): GetAllCategories {
        return GetAllCategories(categoryRepository)
    }
}