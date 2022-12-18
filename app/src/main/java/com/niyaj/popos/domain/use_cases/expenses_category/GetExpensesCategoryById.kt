package com.niyaj.popos.domain.use_cases.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.domain.util.Resource

class GetExpensesCategoryById(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Resource<ExpensesCategory?>{
        return expensesCategoryRepository.getExpensesCategoryById(categoryId)
    }

}