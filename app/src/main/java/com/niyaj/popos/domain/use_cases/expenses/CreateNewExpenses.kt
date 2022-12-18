package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(newExpenses: Expenses): Resource<Boolean>{
        return expensesRepository.createNewExpenses(newExpenses)
    }

}