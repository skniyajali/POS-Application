package com.niyaj.popos.features.expenses_category.domain.use_cases

import com.niyaj.popos.features.common.util.Resource
import com.niyaj.popos.features.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.features.expenses_category.domain.repository.ExpensesCategoryRepository

class UpdateExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(expensesCategory: ExpensesCategory, categoryId: String): Resource<Boolean> {
        return expensesCategoryRepository.updateExpensesCategory(expensesCategory, categoryId)
    }
}