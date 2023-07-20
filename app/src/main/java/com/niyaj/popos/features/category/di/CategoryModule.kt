package com.niyaj.popos.features.category.di

import com.niyaj.popos.features.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    @Provides
    fun provideCategoryRepository(config : RealmConfiguration) : CategoryRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    fun provideCategoryValidationRepository(config : RealmConfiguration) : CategoryValidationRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideGetAllCategoryCases(categoryRepository : CategoryRepository) : GetAllCategories {
        return GetAllCategories(categoryRepository)
    }
}