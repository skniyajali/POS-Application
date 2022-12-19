package com.niyaj.popos.realm.expenses_category.domain.use_cases

import com.niyaj.popos.domain.util.Resource
import com.niyaj.popos.realm.expenses_category.domain.repository.ExpensesCategoryRepository

class DeleteExpensesCategory(
    private val expensesCategoryRepository: ExpensesCategoryRepository
) {

    suspend operator fun invoke(categoryId: String): Resource<Boolean>{
        return expensesCategoryRepository.deleteExpensesCategory(categoryId)
    }
}