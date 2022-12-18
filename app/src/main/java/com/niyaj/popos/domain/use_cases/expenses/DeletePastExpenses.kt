package com.niyaj.popos.domain.use_cases.expenses

import com.niyaj.popos.domain.repository.ExpensesRepository
import com.niyaj.popos.domain.util.Resource

class DeletePastExpenses(private val expensesRepository: ExpensesRepository) {

    suspend operator fun invoke(deleteAll: Boolean): Resource<Boolean> {
        return expensesRepository.deletePastExpanses(deleteAll)
    }
}