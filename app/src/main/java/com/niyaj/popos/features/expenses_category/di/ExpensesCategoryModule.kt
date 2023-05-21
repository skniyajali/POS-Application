package com.niyaj.popos.features.expenses_category.di

import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.features.expenses_category.domain.use_cases.GetAllExpensesCategory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesCategoryModule {
    @Provides
    @Singleton
    fun provideExpensesCategoryUseCases(
        expensesCategoryRepository: ExpensesCategoryRepository
    ): GetAllExpensesCategory {
        return GetAllExpensesCategory(expensesCategoryRepository)
    }
}