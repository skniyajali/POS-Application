package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository

class DeletePastExpenses(private val expensesRepository: ExpensesRepository) {

    suspend operator fun invoke(deleteAll: Boolean): Resource<Boolean> {
        return expensesRepository.deletePastExpenses(deleteAll)
    }
}