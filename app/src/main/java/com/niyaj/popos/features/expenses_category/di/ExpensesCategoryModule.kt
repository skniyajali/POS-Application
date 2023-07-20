package com.niyaj.popos.features.expenses_category.di

import com.niyaj.popos.features.expenses_category.data.repository.ExpensesCategoryRepositoryImpl
import com.niyaj.popos.features.expenses_category.domain.repository.ExpCategoryValidationRepository
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.features.expenses_category.domain.use_cases.GetAllExpensesCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesCategoryModule {

    @Provides
    fun provideExpensesCategoryRepositoryImpl(config : RealmConfiguration) : ExpensesCategoryRepository {
        return ExpensesCategoryRepositoryImpl(config)
    }

    @Provides
    fun provideExpensesCategoryValidationRepositoryImpl(config : RealmConfiguration) : ExpCategoryValidationRepository {
        return ExpensesCategoryRepositoryImpl(config)
    }


    @Provides
    @Singleton
    fun provideExpensesCategoryUseCases(
        expensesCategoryRepository : ExpensesCategoryRepository
    ) : GetAllExpensesCategory {
        return GetAllExpensesCategory(expensesCategoryRepository)
    }
}