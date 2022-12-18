package com.niyaj.popos.domain.use_cases.expenses_category

import com.niyaj.popos.domain.model.ExpensesCategory
import com.niyaj.popos.domain.repository.ExpensesCategoryRepository
import com.niyaj.popos.domain.util.Resource

class UpdateExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(expensesCategory: ExpensesCategory, categoryId: String): Resource<Boolean>{
        return expensesCategoryRepository.updateExpensesCategory(expensesCategory, categoryId)
    }
}