package com.niyaj.popos.features.category.di

import com.niyaj.popos.features.category.domain.repository.CategoryRepository
import com.niyaj.popos.features.category.domain.repository.CategoryValidationRepository
import com.niyaj.popos.features.category.domain.use_cases.CategoryUseCases
import com.niyaj.popos.features.category.domain.use_cases.CreateNewCategory
import com.niyaj.popos.features.category.domain.use_cases.DeleteCategory
import com.niyaj.popos.features.category.domain.use_cases.GetAllCategories
import com.niyaj.popos.features.category.domain.use_cases.GetCategoryById
import com.niyaj.popos.features.category.domain.use_cases.UpdateCategory
import com.niyaj.popos.features.category.domain.use_cases.validation.ValidateCategoryName
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
    fun provideCategoryCases(categoryRepository: CategoryRepository, categoryValidationRepository: CategoryValidationRepository): CategoryUseCases {
        return CategoryUseCases(
            getAllCategories = GetAllCategories(categoryRepository),
            getCategoryById = GetCategoryById(categoryRepository),
            createNewCategory = CreateNewCategory(categoryRepository),
            updateCategory = UpdateCategory(categoryRepository),
            deleteCategory = DeleteCategory(categoryRepository),
            validateCategoryName = ValidateCategoryName(categoryValidationRepository),

        )
    }
}