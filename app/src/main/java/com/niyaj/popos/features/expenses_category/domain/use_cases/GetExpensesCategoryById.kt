package com.niyaj.popos.features.expenses_category.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository

class GetExpensesCategoryById(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Resource<ExpensesCategory?> {
        return expensesCategoryRepository.getExpensesCategoryById(categoryId)
    }
}