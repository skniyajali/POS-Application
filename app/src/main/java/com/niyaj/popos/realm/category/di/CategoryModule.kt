package com.niyaj.popos.realm.category.di

import com.niyaj.popos.realm.category.domain.use_cases.CategoryUseCases
import com.niyaj.popos.realm.category.domain.use_cases.CreateNewCategory
import com.niyaj.popos.realm.category.domain.use_cases.DeleteCategory
import com.niyaj.popos.realm.category.domain.use_cases.FindCategoryByName
import com.niyaj.popos.realm.category.domain.use_cases.GetAllCategories
import com.niyaj.popos.realm.category.domain.use_cases.GetCategoryById
import com.niyaj.popos.realm.category.domain.use_cases.UpdateCategory
import com.niyaj.popos.realm.category.data.repository.CategoryRepositoryImpl
import com.niyaj.popos.realm.category.domain.model.Category
import com.niyaj.popos.realm.category.domain.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CategoryModule {

    private val schema = setOf(Category::class)

    private val config = RealmConfiguration
        .Builder(schema)
        .deleteRealmIfMigrationNeeded()
        .name("category.realm")
        .log(LogLevel.ALL)
        .build()


    @Provides
    fun provideCategoryRealmDaoImpl(): CategoryRepository {
        return CategoryRepositoryImpl(config)
    }

    @Provides
    @Singleton
    fun provideCategoryCases(categoryRepository: CategoryRepository): CategoryUseCases {
        return CategoryUseCases(
            getAllCategories = GetAllCategories(categoryRepository),
            getCategoryById = GetCategoryById(categoryRepository),
            findCategoryByName = FindCategoryByName(categoryRepository),
            createNewCategory = CreateNewCategory(categoryRepository),
            updateCategory = UpdateCategory(categoryRepository),
            deleteCategory = DeleteCategory(categoryRepository),
        )
    }


}