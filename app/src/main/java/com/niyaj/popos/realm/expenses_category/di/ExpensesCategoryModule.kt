package com.niyaj.popos.realm.expenses_category.di

import com.niyaj.popos.realm.expenses_category.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.realm.expenses_category.domain.use_cases.CreateNewExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.use_cases.DeleteExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.use_cases.ExpensesCategoryUseCases
import com.niyaj.popos.realm.expenses_category.domain.use_cases.GetAllExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.use_cases.GetExpensesCategoryById
import com.niyaj.popos.realm.expenses_category.domain.use_cases.UpdateExpensesCategory
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
    fun provideExpensesCategoryUseCases(expensesCategoryRepository: ExpensesCategoryRepository): ExpensesCategoryUseCases {
        return ExpensesCategoryUseCases(
            getAllExpensesCategory = GetAllExpensesCategory(expensesCategoryRepository),
            getExpensesCategoryById = GetExpensesCategoryById(expensesCategoryRepository),
            createNewExpensesCategory = CreateNewExpensesCategory(expensesCategoryRepository),
            updateExpensesCategory = UpdateExpensesCategory(expensesCategoryRepository),
            deleteExpensesCategory = DeleteExpensesCategory(expensesCategoryRepository)
        )
    }
}