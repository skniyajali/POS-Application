package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource

class UpdateExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(newExpenses: Expenses, expansesId: String): Resource<Boolean>{
        return expensesRepository.updateExpenses(newExpenses, expansesId)
    }

}