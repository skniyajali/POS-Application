package com.niyaj.popos.features.expenses.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses.domain.repository.ExpensesRepository

class DeleteExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(expensesId: String): Resource<Boolean> {
        return expensesRepository.deleteExpenses(expensesId)
    }
}