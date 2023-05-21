package com.niyaj.popos.features.expenses.di

import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.use_cases.GetAllExpenses
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExpensesModule {

    @Provides
    @Singleton
    fun provideGetAllExpensesUseCases(expensesRepository: ExpensesRepository): GetAllExpenses {
        return GetAllExpenses(expensesRepository)
    }
}