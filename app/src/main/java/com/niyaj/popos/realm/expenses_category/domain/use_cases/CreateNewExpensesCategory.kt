package com.niyaj.popos.realm.expenses_category.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.repository.ExpensesCategoryRepository

class CreateNewExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {
    suspend operator fun invoke(newExpensesCategory: ExpensesCategory): Resource<Boolean>{
        return expensesCategoryRepository.createNewExpensesCategory(newExpensesCategory)
    }
}