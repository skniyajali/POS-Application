package com.niyaj.popos.realm.expenses.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses.domain.repository.ExpensesRepository

class DeleteExpenses(
    private val expensesRepository: ExpensesRepository
) {

    suspend operator fun invoke(expensesId: String): Resource<Boolean>{
        return expensesRepository.deleteExpenses(expensesId)
    }
}