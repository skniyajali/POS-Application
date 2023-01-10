package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.model.Expenses
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository

class CreateNewExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(newExpenses: Expenses): Resource<Boolean> {
        return expensesRepository.createNewExpenses(newExpenses)
    }
}