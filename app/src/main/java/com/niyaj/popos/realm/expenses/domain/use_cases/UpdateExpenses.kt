package com.niyaj.popos.realm.expenses.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.domain.model.Expenses
import com.niyaj.popos.realm.expenses.domain.repository.ExpensesRepository

class UpdateExpenses(
    private val expensesRepository: ExpensesRepository
) {
    suspend operator fun invoke(newExpenses: Expenses, expensesId: String): Resource<Boolean>{
        return expensesRepository.updateExpenses(newExpenses, expensesId)
    }
}