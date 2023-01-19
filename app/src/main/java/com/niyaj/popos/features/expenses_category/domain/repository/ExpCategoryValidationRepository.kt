package com.niyaj.popos.features.expenses_category.domain.repository

import com.niyaj.popos.features.common.util.ValidationResult

interface ExpCategoryValidationRepository {
    fun validateExpensesCategoryName(categoryName: String): ValidationResult
}