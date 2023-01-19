package com.niyaj.popos.features.expenses_category.domain.use_cases.validation

import com.niyaj.popos.features.common.util.ValidationResult
import com.niyaj.popos.features.expenses_category.domain.repository.ExpCategoryValidationRepository
import javax.inject.Inject

class ValidateExpensesCategoryName @Inject constructor(
    private val expCategoryValidationRepository: ExpCategoryValidationRepository
) {

    operator fun invoke(categoryName: String): ValidationResult {
        return expCategoryValidationRepository.validateExpensesCategoryName(categoryName)
    }
}