package com.niyaj.popos.domain.use_cases.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.domain.util.Resource

class CreateNewExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(newExpensesCategory: ExpensesCategory): Resource<Boolean>{
        return expensesCategoryRepository.createNewExpensesCategory(newExpensesCategory)
    }
}