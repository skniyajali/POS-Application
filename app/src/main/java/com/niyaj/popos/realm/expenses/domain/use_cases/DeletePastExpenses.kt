package com.niyaj.popos.realm.expenses.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.domain.repository.ExpensesRepository

class DeletePastExpenses(private val expensesRepository: ExpensesRepository) {

    suspend operator fun invoke(deleteAll: Boolean): Resource<Boolean> {
        return expensesRepository.deletePastExpenses(deleteAll)
    }
}