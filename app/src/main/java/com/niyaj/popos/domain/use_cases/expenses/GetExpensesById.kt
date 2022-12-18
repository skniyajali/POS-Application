package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.model.Expenses
import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource

class GetExpensesById(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(expansesId: String): Resource<Expenses?>{
        return expensesRepository.getExpensesById(expansesId)
    }
}