package com.niyaj.popos.realm.expenses_category.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses_category.domain.model.ExpensesCategory
import com.niyaj.popos.realm.expenses_category.domain.repository.ExpensesCategoryRepository

class UpdateExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(expensesCategory: ExpensesCategory, categoryId: String): Resource<Boolean>{
        return expensesCategoryRepository.updateExpensesCategory(expensesCategory, categoryId)
    }
}