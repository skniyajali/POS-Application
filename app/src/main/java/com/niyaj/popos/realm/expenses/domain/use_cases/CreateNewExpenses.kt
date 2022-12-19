package com.niyaj.popos.realm.expenses.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.domain.model.Expenses
import com.niyaj.popos.realm.expenses.domain.repository.ExpensesRepository

class CreateNewExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(newExpenses: Expenses): Resource<Boolean>{
        return expensesRepository.createNewExpenses(newExpenses)
    }

}