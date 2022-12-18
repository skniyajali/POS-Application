package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource

class DeleteExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(expansesId: String): Resource<Boolean>{
        return expensesRepository.deleteExpenses(expansesId)
    }
}