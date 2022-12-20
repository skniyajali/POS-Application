package com.niyaj.popos.features.expenses.di

import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository
import com.niyaj.popos.features.expenses.domain.use_cases.CreateNewExpenses
import com.niyaj.popos.features.expenses.domain.use_cases.DeleteExpenses
import com.niyaj.popos.features.expenses.domain.use_cases.DeletePastExpenses
import com.niyaj.popos.features.expenses.domain.use_cases.ExpensesUseCases
import com.niyaj.popos.features.expenses.domain.use_cases.GetAllExpenses
import com.niyaj.popos.features.expenses.domain.use_cases.GetExpensesById
import com.niyaj.popos.features.expenses.domain.use_cases.UpdateExpenses
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
    fun provideExpensesUseCases(expensesRepository: ExpensesRepository): ExpensesUseCases {
        return ExpensesUseCases(
            getAllExpenses = GetAllExpenses(expensesRepository),
            getExpensesById = GetExpensesById(expensesRepository),
            createNewExpenses = CreateNewExpenses(expensesRepository),
            updateExpenses = UpdateExpenses(expensesRepository),
            deleteExpenses = DeleteExpenses(expensesRepository),
            deletePastExpenses = DeletePastExpenses(expensesRepository),
        )
    }
}